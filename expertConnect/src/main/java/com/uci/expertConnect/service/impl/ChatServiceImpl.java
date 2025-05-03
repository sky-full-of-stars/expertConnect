package com.uci.expertConnect.service.impl;

import com.uci.expertConnect.dto.request.ChatRequest;
import com.uci.expertConnect.dto.response.ChatResponse;
import com.uci.expertConnect.service.ChatService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ChatServiceImpl implements ChatService {

    private final WebClient webClient;

    public ChatServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8000").build();
    }

    @Override
    public String getChatReply(String userId, String message) {
        ChatRequest request = new ChatRequest();
        request.setUserId(userId);
        request.setMessage(message);

        Mono<ChatResponse> responseMono = webClient.post()
                .uri("/chat")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ChatResponse.class);

        ChatResponse response = responseMono.block(); // synchronous call
        return response != null ? response.getReply() : "No response from assistant.";
    }
}
