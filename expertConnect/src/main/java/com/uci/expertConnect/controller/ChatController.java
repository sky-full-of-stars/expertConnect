package com.uci.expertConnect.controller;

import com.uci.expertConnect.dto.request.ChatRequest;
import com.uci.expertConnect.service.ChatService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public String chat(@RequestBody ChatRequest request) {
        return chatService.getChatReply(request.getUserId(), request.getMessage());
    }
}
