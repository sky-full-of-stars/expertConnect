package com.uci.expertConnect.service;

import com.uci.expertConnect.dto.BookingRequest;
import com.uci.expertConnect.dto.BookingResponse;
import com.uci.expertConnect.model.Booking;

public interface BookingService {
    BookingResponse createBooking(BookingRequest request);
    void processCompletedMeetings();
} 