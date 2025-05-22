package com.uci.expertConnect.controller;

import com.uci.expertConnect.dto.request.CreateMeetingRequest;
import com.uci.expertConnect.model.Meeting;
import com.uci.expertConnect.service.MeetingService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/meetings")
//still have not tested the endpoints
public class MeetingsController {
    private static final Logger logger = LoggerFactory.getLogger(MeetingsController.class);
    private final MeetingService meetingService;

    @Autowired
    public MeetingsController(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @PostMapping
    public ResponseEntity<Meeting> createMeeting(@Valid @RequestBody CreateMeetingRequest request) {
        logger.info("Received request to create meeting between expert {} and user {}", 
            request.getExpertId(), request.getUserId());
        Meeting meeting = meetingService.createMeeting(request);
        logger.info("Successfully created meeting with ID: {}", meeting.getId());
        return ResponseEntity.ok(meeting);
    }

    @GetMapping("/{meetingId}")
    public ResponseEntity<Meeting> getMeeting(@PathVariable Long meetingId) {
        logger.info("Received request to get meeting with ID: {}", meetingId);
        Meeting meeting = meetingService.getMeetingById(meetingId);
        logger.info("Successfully retrieved meeting with ID: {}", meetingId);
        return ResponseEntity.ok(meeting);
    }

    @PostMapping("/{meetingId}/send-link")
    public ResponseEntity<Void> sendMeetingLink(@PathVariable Long meetingId) {
        logger.info("Received request to send meeting link for meeting ID: {}", meetingId);
        Meeting meeting = meetingService.getMeetingById(meetingId);
        meetingService.sendMeetingLink(meeting);
        logger.info("Successfully sent meeting link for meeting ID: {}", meetingId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{meetingId}/send-recording")
    public ResponseEntity<Void> sendRecordingLink(@PathVariable Long meetingId) {
        logger.info("Received request to send recording link for meeting ID: {}", meetingId);
        Meeting meeting = meetingService.getMeetingById(meetingId);
        meetingService.sendRecordingLink(meeting);
        logger.info("Successfully sent recording link for meeting ID: {}", meetingId);
        return ResponseEntity.ok().build();
    }
} 