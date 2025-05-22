package com.uci.expertConnect.service.impl;

import com.uci.expertConnect.dto.request.CreateMeetingRequest;
import com.uci.expertConnect.exception.NotFoundException;
import com.uci.expertConnect.model.Expert;
import com.uci.expertConnect.model.Meeting;
import com.uci.expertConnect.model.User;
import com.uci.expertConnect.repository.ExpertRepository;
import com.uci.expertConnect.repository.MeetingRepository;
import com.uci.expertConnect.repository.UserRepository;
import com.uci.expertConnect.service.EmailService;
import com.uci.expertConnect.service.MeetingService;
import com.uci.expertConnect.service.ZoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MeetingServiceImpl implements MeetingService {

    private final MeetingRepository meetingRepository;
    private final ExpertRepository expertRepository;
    private final UserRepository userRepository;
    private final ZoomService zoomService;
    private final EmailService emailService;

    @Autowired
    public MeetingServiceImpl(
            MeetingRepository meetingRepository,
            ExpertRepository expertRepository,
            UserRepository userRepository,
            ZoomService zoomService,
            EmailService emailService) {
        this.meetingRepository = meetingRepository;
        this.expertRepository = expertRepository;
        this.userRepository = userRepository;
        this.zoomService = zoomService;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public Meeting createMeeting(CreateMeetingRequest request) {
        Expert expert = expertRepository.findById(request.getExpertId())
                .orElseThrow(() -> new NotFoundException("Expert not found with ID: " + request.getExpertId()));
        
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + request.getUserId()));

        // Create meeting in Zoom
        String joinUrl = zoomService.createMeeting(
            "Meeting between " + expert.getUser().getName() + " and " + user.getName(),
            request.getMeetingDateTime(),
            expert.getUser().getEmail()
        );

        // Create meeting in database
        Meeting meeting = new Meeting();
        meeting.setExpert(expert);
        meeting.setUser(user);
        meeting.setMeetingDateTime(request.getMeetingDateTime());
        meeting.setZoomJoinUrl(joinUrl);
        meeting.setStatus(Meeting.MeetingStatus.SCHEDULED);

        Meeting savedMeeting = meetingRepository.save(meeting);
        
        // Send meeting links via email
        emailService.sendMeetingLink(savedMeeting);
        
        return savedMeeting;
    }

    @Override
    public Meeting getMeetingById(Long meetingId) {
        return meetingRepository.findById(meetingId)
                .orElseThrow(() -> new NotFoundException("Meeting not found with ID: " + meetingId));
    }

    @Override
    public void sendMeetingLink(Meeting meeting) {
        emailService.sendMeetingLink(meeting);
    }

    @Override
    public void sendRecordingLink(Meeting meeting) {
        // Get recording URL from Zoom
        String recordingUrl = zoomService.getRecordingUrl(meeting.getZoomMeetingId());
        meeting.setRecordingUrl(recordingUrl);
        meetingRepository.save(meeting);
        
        // Send recording link via email
        emailService.sendRecordingLink(meeting);
    }
} 