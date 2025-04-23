package com.uci.expertConnect.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateMeetingRequest {
    @NotNull(message = "Expert ID is required")
    private Long expertId;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Meeting date and time is required")
    private LocalDateTime meetingDateTime;
} 