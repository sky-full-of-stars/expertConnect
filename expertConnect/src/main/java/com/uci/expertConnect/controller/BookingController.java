package com.uci.expertConnect.controller;

import com.uci.expertConnect.dto.BookingRequest;
import com.uci.expertConnect.dto.BookingResponse;
import com.uci.expertConnect.dto.response.BookingListResponse;
import com.uci.expertConnect.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@RequestBody BookingRequest request) {
        BookingResponse response = bookingService.createBooking(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/expert/{expertId}")
    public ResponseEntity<List<BookingListResponse>> getExpertBookings(@PathVariable String expertId) {
        List<BookingListResponse> bookings = bookingService.getExpertBookings(expertId);
        return ResponseEntity.ok(bookings);
    }
} 