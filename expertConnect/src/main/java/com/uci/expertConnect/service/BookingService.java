package com.uci.expertConnect.service;

import com.uci.expertConnect.dto.BookingRequest;
import com.uci.expertConnect.dto.BookingResponse;
import com.uci.expertConnect.dto.response.BookingListResponse;
import com.uci.expertConnect.model.Booking;
import java.util.List;

public interface BookingService {
    BookingResponse createBooking(BookingRequest request);
    void processCompletedMeetings();
    List<BookingListResponse> getExpertBookings(String expertId);
} 