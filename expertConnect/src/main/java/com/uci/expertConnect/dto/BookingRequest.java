package com.uci.expertConnect.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class BookingRequest {
    private String expertId;
    private String userId;
    private LocalDate bookingDate;
    private LocalTime bookingTime;
    private Integer duration;
    private Double price;
} 