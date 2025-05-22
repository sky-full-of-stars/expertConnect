package com.uci.expertConnect.repository;

import com.uci.expertConnect.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByOrderId(Long orderId);
    
    Optional<Transaction> findByStripePaymentIntentId(String paymentIntentId);
    
    Optional<Transaction> findByStripeSessionId(String sessionId);
    
    List<Transaction> findByStatus(Transaction.TransactionStatus status);
    
    @Query("SELECT t FROM Transaction t WHERE t.order.id = :orderId AND t.status = :status")
    List<Transaction> findByOrderIdAndStatus(@Param("orderId") Long orderId, @Param("status") Transaction.TransactionStatus status);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.order.id = :orderId AND t.status = 'COMPLETED'")
    Double getTotalPaidAmountForOrder(@Param("orderId") Long orderId);
    
    @Query("SELECT SUM(t.refundAmount) FROM Transaction t WHERE t.order.id = :orderId AND t.status = 'REFUNDED'")
    Double getTotalRefundedAmountForOrder(@Param("orderId") Long orderId);
} 