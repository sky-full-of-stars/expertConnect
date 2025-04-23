package com.uci.expertConnect.service.impl;

import com.uci.expertConnect.service.ZoomService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class ZoomServiceImpl implements ZoomService {

    @Value("${zoom.api.key}")
    private String apiKey;

    @Value("${zoom.api.secret}")
    private String apiSecret;

    @Value("${zoom.api.account.id}")
    private String accountId;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String ZOOM_API_BASE_URL = "https://api.zoom.us/v2";

    @Override
    public String createMeeting(String topic, LocalDateTime startTime, String email) {
        String url = ZOOM_API_BASE_URL + "/users/" + email + "/meetings";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + getAccessToken());
        
        Map<String, Object> body = new HashMap<>();
        body.put("topic", topic);
        body.put("type", 2); // Scheduled meeting
        body.put("start_time", startTime.format(DateTimeFormatter.ISO_DATE_TIME));
        body.put("duration", 60); // 1 hour
        body.put("settings", Map.of(
            "host_video", true,
            "participant_video", true,
            "join_before_host", false,
            "mute_upon_entry", true,
            "waiting_room", true,
            "recording_privilege", "host",
            "auto_recording", "cloud"
        ));
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
        
        Map<String, Object> meetingData = response.getBody();
        return (String) meetingData.get("join_url");
    }

    @Override
    public String getRecordingUrl(String meetingId) {
        String url = ZOOM_API_BASE_URL + "/meetings/" + meetingId + "/recordings";
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getAccessToken());
        
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
        
        Map<String, Object> recordingData = response.getBody();
        return (String) recordingData.get("download_url");
    }

    private String getAccessToken() {
        String url = "https://zoom.us/oauth/token";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(apiKey, apiSecret);
        
        String body = "grant_type=account_credentials&account_id=" + accountId;
        
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
        
        Map<String, Object> tokenData = response.getBody();
        return (String) tokenData.get("access_token");
    }
} 