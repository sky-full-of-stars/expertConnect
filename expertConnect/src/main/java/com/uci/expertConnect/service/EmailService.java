package com.uci.expertConnect.service;

import com.uci.expertConnect.model.Meeting;

public interface EmailService {
    void sendMeetingLink(Meeting meeting);
    void sendRecordingLink(Meeting meeting);
} 