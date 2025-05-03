package com.uci.expertConnect.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatResponse {

    @NotBlank(message = "Reply from chatgpt cannot be blank")
    private String reply;
}
