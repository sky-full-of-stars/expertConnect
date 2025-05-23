package com.uci.expertConnect.service.impl;

import com.uci.expertConnect.dto.BookingRequest;
import com.uci.expertConnect.dto.BookingResponse;
import com.uci.expertConnect.dto.request.CreateMeetingRequest;
import com.uci.expertConnect.model.Booking;
import com.uci.expertConnect.model.BookingStatus;
import com.uci.expertConnect.model.Meeting;
import com.uci.expertConnect.repository.BookingRepository;
import com.uci.expertConnect.service.BookingService;
import com.uci.expertConnect.service.MeetingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final MeetingService meetingService;

    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        try {
            // Create meeting using MeetingService
            CreateMeetingRequest meetingRequest = new CreateMeetingRequest();
            meetingRequest.setExpertId(Long.parseLong(request.getExpertId()));
            meetingRequest.setUserId(Long.parseLong(request.getUserId()));
            meetingRequest.setMeetingDateTime(LocalDateTime.of(request.getBookingDate(), request.getBookingTime()));
            
            Meeting meeting = meetingService.createMeeting(meetingRequest);
            
            // Create booking record
            Booking booking = new Booking();
            booking.setUserId(request.getUserId());
            booking.setExpertId(request.getExpertId());
            booking.setBookingDate(request.getBookingDate());
            booking.setBookingTime(request.getBookingTime());
            booking.setDuration(request.getDuration());
            booking.setPrice(request.getPrice());
            booking.setMeetingId(meeting.getId().toString());
            booking.setMeetingLink(meeting.getZoomJoinUrl());
            booking.setStatus(BookingStatus.CONFIRMED);

            booking = bookingRepository.save(booking);

            // Send meeting link
            meetingService.sendMeetingLink(meeting);

            return mapToResponse(booking);
        } catch (Exception e) {
            log.error("Failed to create booking: {}", e.getMessage());
            throw new RuntimeException("Failed to create meeting: " + e.getMessage());
        }
    }

    @Override
    @Scheduled(fixedRate = 300000) // Run every 5 minutes
    @Transactional
    public void processCompletedMeetings() {
        try {
            var completedMeetings = bookingRepository.findCompletedMeetingsWithoutRecording();
            
            for (Booking booking : completedMeetings) {
                try {
                    Meeting meeting = meetingService.getMeetingById(Long.parseLong(booking.getMeetingId()));
                    meetingService.sendRecordingLink(meeting);
                    booking.setRecordingLink(meeting.getRecordingUrl());
                    booking.setStatus(BookingStatus.COMPLETED);
                    bookingRepository.save(booking);
                } catch (Exception e) {
                    log.error("Failed to process recording for meeting {}: {}", 
                             booking.getMeetingId(), e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Failed to process completed meetings: {}", e.getMessage());
        }
    }

    private BookingResponse mapToResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setBookingId(booking.getId());
        response.setMeetingId(booking.getMeetingId());
        response.setMeetingLink(booking.getMeetingLink());
        response.setStatus(booking.getStatus());
        response.setCreatedAt(booking.getCreatedAt());
        return response;
    }
} 