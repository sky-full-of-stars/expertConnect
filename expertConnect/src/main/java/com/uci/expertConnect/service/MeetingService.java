package com.uci.expertConnect.service;

import com.uci.expertConnect.dto.request.CreateMeetingRequest;
import com.uci.expertConnect.model.Meeting;

public interface MeetingService {
    Meeting createMeeting(CreateMeetingRequest request);
    Meeting getMeetingById(Long meetingId);
    void sendMeetingLink(Meeting meeting);
    void sendRecordingLink(Meeting meeting);
} 