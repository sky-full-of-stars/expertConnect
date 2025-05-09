package com.uci.expertConnect.controller;

import com.uci.expertConnect.dto.request.ChatRequest;
import com.uci.expertConnect.service.ChatService;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/v1/chat")
public class ChatController {
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public String chat(@RequestBody ChatRequest request) {
        logger.debug("Received chat request from userId={}, message='{}'", request.getUserId(), request.getMessage());

        String reply = chatService.getChatReply(request.getUserId(), request.getMessage());

        logger.debug("Generated reply='{}'", reply);
        return reply;
    }
}
