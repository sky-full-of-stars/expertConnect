package com.uci.expertConnect.controller;

import com.uci.expertConnect.dto.request.ChatRequest;
import com.uci.expertConnect.dto.request.FindMatchingExpertsRequest;
import com.uci.expertConnect.dto.response.ChatResponse;
import com.uci.expertConnect.service.ChatService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.uci.expertConnect.service.ExpertMatchingService;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/chat")
public class ChatController {
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    private final ChatService chatService;
    private final ExpertMatchingService expertMatchingService;

    public ChatController(ChatService chatService, ExpertMatchingService expertMatchingService) {
        this.chatService = chatService;
        this.expertMatchingService = expertMatchingService;
    }
    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {
        logger.info("Received chat request from userId={}, message='{}'", request.getUserId(), request.getMessage());

        ChatResponse chatResponse = chatService.getChatReply(request.getUserId(), request.getMessage());

        logger.info("Generated reply='{}' and retrieveProfiles='{}'", chatResponse.getReply(), chatResponse.isRetrieveProfiles());
        // If retrieveProfiles is true, call the summarizeConversation API
        if (chatResponse.isRetrieveProfiles()) {
            logger.info("retrieveProfiles is true. Summarizing conversation...");
            String summarizedConvo = chatService.summarizeConversation(request.getUserId());
            logger.info("Recieved convo summary...");

            FindMatchingExpertsRequest matchRequest = new FindMatchingExpertsRequest();
            matchRequest.setUserId(request.getUserId());
            matchRequest.setText(summarizedConvo);
            // Call the expert matching service directly with the summarized text
            List<Integer> matchingExperts = expertMatchingService.findMatchingExperts(matchRequest);
            logger.info("Matching expert IDs returned: {}", matchingExperts);

            // Now call the Python FastAPI Redis service to store them
            try {
                String fastApiUrl = "http://localhost:8003/store-matching-experts";  // Adjust if deployed elsewhere

                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                // Build the request body
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("userId", request.getUserId());
                requestBody.put("matchingExperts", matchingExperts);

                HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
                ResponseEntity<String> response = restTemplate.postForEntity(fastApiUrl, requestEntity, String.class);

                logger.info("Stored matching experts in Redis via FastAPI. Response: {}", response.getBody());

            } catch (Exception e) {
                logger.error("Failed to call /store-matching-experts API: {}", e.getMessage());
            }
       }
        return chatResponse;
    }

    @PostMapping("/summarize-chat")
    public String summarizeConversation(@RequestBody ChatRequest request) {
        logger.info("Received request to summarize conversation for userId={}", request.getUserId());

        // Call the summarize_convo service and return the result
        String summarizedConvo = chatService.summarizeConversation(request.getUserId());

        logger.info("Summarized conversation: '{}'", summarizedConvo);

        return summarizedConvo;
    }
}
