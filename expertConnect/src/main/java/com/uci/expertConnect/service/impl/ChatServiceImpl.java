package com.uci.expertConnect.service.impl;

import com.uci.expertConnect.dto.request.ChatRequest;
import com.uci.expertConnect.dto.response.ChatResponse;
import com.uci.expertConnect.service.ChatService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class ChatServiceImpl implements ChatService {

    private final WebClient webClient;

    public ChatServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8000").build();
    }

    @Override
    public ChatResponse getChatReply(String userId, String message) {
        ChatRequest request = new ChatRequest();
        request.setUserId(userId);
        request.setMessage(message);

        Mono<ChatResponse> responseMono = webClient.post()
                .uri("/chat")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ChatResponse.class);

        ChatResponse response = responseMono.block(); // synchronous call
        return response != null ? response : new ChatResponse();
    }

    @Override
    public String summarizeConversation(String userId) {
        WebClient summarizeClient = WebClient.builder()
                .baseUrl("http://localhost:8001") // Hardcoded base URL
                .build();

        Map<String, String> payload = new HashMap<>();
        payload.put("userId", userId);

        Mono<String> responseMono = summarizeClient.post()
                .uri("/summarize-chat")
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class);

        return responseMono.block(); // returns summarized string
    }

}
