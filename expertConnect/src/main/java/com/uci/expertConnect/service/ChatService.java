package com.uci.expertConnect.service;

import com.uci.expertConnect.dto.response.ChatResponse;

public interface ChatService {
    ChatResponse getChatReply(String userId, String message);
    String summarizeConversation(String userId);

}
