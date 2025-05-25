package com.uci.expertConnect.dto.response;

import com.uci.expertConnect.model.BookingStatus;
import com.uci.expertConnect.model.PaymentStatus;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
public class BookingListResponse {
    private Long id;
    private String userId;
    private LocalDate bookingDate;
    private LocalTime bookingTime;
    private Integer duration;
    private Double price;
    private String meetingId;
    private String meetingLink;
    private String recordingLink;
    private BookingStatus status;
    private PaymentStatus paymentStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 