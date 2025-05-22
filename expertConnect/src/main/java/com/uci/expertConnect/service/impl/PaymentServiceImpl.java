package com.uci.expertConnect.service.impl;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import com.uci.expertConnect.dto.request.CreateCheckoutSessionRequest;
import com.uci.expertConnect.dto.response.CheckoutSessionResponse;
import com.uci.expertConnect.exception.ResourceNotFoundException;
import com.uci.expertConnect.model.Order;
import com.uci.expertConnect.model.Order.OrderStatus;
import com.uci.expertConnect.model.Transaction;
import com.uci.expertConnect.model.Transaction.TransactionStatus;
import com.uci.expertConnect.repository.ExpertRepository;
import com.uci.expertConnect.repository.OrderRepository;
import com.uci.expertConnect.repository.TransactionRepository;
import com.uci.expertConnect.repository.UserRepository;
import com.uci.expertConnect.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @Value("${stripe.success.url}")
    private String successUrl;

    @Value("${stripe.cancel.url}")
    private String cancelUrl;

    private final OrderRepository orderRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final ExpertRepository expertRepository;

    @Autowired
    public PaymentServiceImpl(
            OrderRepository orderRepository,
            TransactionRepository transactionRepository,
            UserRepository userRepository,
            ExpertRepository expertRepository) {
        this.orderRepository = orderRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.expertRepository = expertRepository;
    }

    @Override
    @Transactional
    public CheckoutSessionResponse createCheckoutSession(CreateCheckoutSessionRequest request) {
        try {
            Order order = orderRepository.findById(request.getOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + request.getOrderId()));
            
            // Create a new transaction record
            Transaction transaction = new Transaction();
            transaction.setOrder(order);
            transaction.setAmount(order.getTotalAmount());
            transaction.setCurrency("USD");
            transaction.setStatus(TransactionStatus.PENDING);
            transaction.setCreatedAt(LocalDateTime.now());
            transaction = transactionRepository.save(transaction);
            
            // Format amount as cents
            long amountInCents = Math.round(order.getTotalAmount() * 100); 
            
            // Create description
            String description = "Expert Connect Session with " + order.getExpert().getUser().getName();
            
            // Create Stripe Checkout Session
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl(cancelUrl)
                    .setClientReferenceId(order.getId().toString())
                    .putMetadata("orderId", order.getId().toString())
                    .putMetadata("transactionId", transaction.getId().toString())
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("usd")
                                                    .setUnitAmount(amountInCents)
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName("Expert Consultation")
                                                                    .setDescription(description)
                                                                    .build()
                                                    ).build()
                                    ).build()
                    ).build();

            Session session = Session.create(params);
            
            // Update transaction with session ID
            transaction.setStripeSessionId(session.getId());
            transactionRepository.save(transaction);
            
            return CheckoutSessionResponse.builder()
                    .sessionId(session.getId())
                    .sessionUrl(session.getUrl())
                    .status("created")
                    .orderId(order.getId())
                    .message("Checkout session created successfully")
                    .build();
            
        } catch (StripeException e) {
            logger.error("Error creating Stripe checkout session", e);
            return CheckoutSessionResponse.builder()
                    .status("error")
                    .message("Failed to create checkout session: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            logger.error("Unexpected error creating checkout session", e);
            return CheckoutSessionResponse.builder()
                    .status("error")
                    .message("An unexpected error occurred: " + e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional
    public String handleWebhookEvent(String payload, String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
            
            // Handle the event
            switch (event.getType()) {
                case "checkout.session.completed":
                    handleCheckoutSessionCompleted(event);
                    break;
                case "payment_intent.succeeded":
                    handlePaymentIntentSucceeded(event);
                    break;
                case "payment_intent.payment_failed":
                    handlePaymentIntentFailed(event);
                    break;
                default:
                    logger.info("Unhandled event type: {}", event.getType());
            }
            
            return "Webhook processed successfully";
        } catch (SignatureVerificationException e) {
            logger.error("Invalid signature on Stripe webhook", e);
            return "Invalid signature";
        } catch (Exception e) {
            logger.error("Error processing webhook", e);
            return "Error processing webhook: " + e.getMessage();
        }
    }

    private void handleCheckoutSessionCompleted(Event event) {
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        if (dataObjectDeserializer.getObject().isPresent()) {
            Session session = (Session) dataObjectDeserializer.getObject().get();
            
            // Process session completion
            String sessionId = session.getId();
            processPaymentSuccess(sessionId);
        } else {
            logger.error("Unable to deserialize session object from event");
        }
    }

    private void handlePaymentIntentSucceeded(Event event) {
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        if (dataObjectDeserializer.getObject().isPresent()) {
            PaymentIntent paymentIntent = (PaymentIntent) dataObjectDeserializer.getObject().get();
            
            // Update transaction with payment intent details
            Optional<Transaction> transaction = transactionRepository.findByStripePaymentIntentId(paymentIntent.getId());
            if (transaction.isPresent()) {
                Transaction t = transaction.get();
                t.setStatus(TransactionStatus.COMPLETED);
                t.setPaymentDate(LocalDateTime.now());
                t.setPaymentMethod(paymentIntent.getPaymentMethod());
                transactionRepository.save(t);
                
                // Update order status
                Order order = t.getOrder();
                order.setStatus(OrderStatus.CONFIRMED);
                orderRepository.save(order);
            } else {
                logger.warn("Transaction not found for payment intent: {}", paymentIntent.getId());
            }
        } else {
            logger.error("Unable to deserialize payment intent object from event");
        }
    }

    private void handlePaymentIntentFailed(Event event) {
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        if (dataObjectDeserializer.getObject().isPresent()) {
            PaymentIntent paymentIntent = (PaymentIntent) dataObjectDeserializer.getObject().get();
            
            // Update transaction with failure details
            Optional<Transaction> transaction = transactionRepository.findByStripePaymentIntentId(paymentIntent.getId());
            if (transaction.isPresent()) {
                Transaction t = transaction.get();
                t.setStatus(TransactionStatus.FAILED);
                t.setErrorMessage(paymentIntent.getLastPaymentError() != null ? 
                        paymentIntent.getLastPaymentError().getMessage() : "Payment failed");
                transactionRepository.save(t);
            } else {
                logger.warn("Transaction not found for payment intent: {}", paymentIntent.getId());
            }
        } else {
            logger.error("Unable to deserialize payment intent object from event");
        }
    }

    // Keep this as a private helper method for the webhook
    private boolean processPaymentSuccess(String sessionId) {
        try {
            // Retrieve the session to get payment intent details
            Session session = Session.retrieve(sessionId);
            Optional<Transaction> transactionOpt = transactionRepository.findByStripeSessionId(sessionId);
            
            if (transactionOpt.isPresent()) {
                Transaction transaction = transactionOpt.get();
                transaction.setStatus(TransactionStatus.COMPLETED);
                transaction.setPaymentDate(LocalDateTime.now());
                transaction.setStripePaymentIntentId(session.getPaymentIntent());
                
                // Get the payment method from the PaymentIntent
                if (session.getPaymentIntent() != null) {
                    PaymentIntent paymentIntent = PaymentIntent.retrieve(session.getPaymentIntent());
                    transaction.setPaymentMethod(paymentIntent.getPaymentMethod());
                }
                
                transactionRepository.save(transaction);
                
                // Update order status
                Order order = transaction.getOrder();
                order.setStatus(OrderStatus.CONFIRMED);
                orderRepository.save(order);
                
                return true;
            } else {
                logger.warn("Transaction not found for session ID: {}", sessionId);
                return false;
            }
        } catch (StripeException e) {
            logger.error("Error retrieving Stripe session or payment intent", e);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error processing payment success", e);
            return false;
        }
    }
} 