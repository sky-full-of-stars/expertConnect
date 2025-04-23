package com.uci.expertConnect.service;

import java.time.LocalDateTime;

public interface ZoomService {
    String createMeeting(String topic, LocalDateTime startTime, String email);
    String getRecordingUrl(String meetingId);
} 