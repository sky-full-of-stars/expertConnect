package com.uci.expertConnect.dto;

import com.uci.expertConnect.model.BookingStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookingResponse {
    private Long bookingId;
    private String meetingId;
    private String meetingLink;
    private BookingStatus status;
    private LocalDateTime createdAt;
} 