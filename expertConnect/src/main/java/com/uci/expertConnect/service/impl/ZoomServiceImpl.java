package com.uci.expertConnect.service.impl;

import com.uci.expertConnect.service.ZoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class ZoomServiceImpl implements ZoomService {
    private static final Logger logger = LoggerFactory.getLogger(ZoomServiceImpl.class);

    @Value("${zoom.api.key}")
    private String apiKey;

    @Value("${zoom.api.secret}")
    private String apiSecret;

    @Value("${zoom.api.account.id}")
    private String accountId;

    @Value("${zoom.api.user.email}")
    private String zoomUserEmail;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String ZOOM_API_BASE_URL = "https://api.zoom.us/v2";

    @Override
    public String createMeeting(String topic, LocalDateTime startTime, String email) {
        try {
            logger.info("Creating Zoom meeting for topic: {}, startTime: {}, email: {}", topic, startTime, email);
            String url = ZOOM_API_BASE_URL + "/users/" + zoomUserEmail + "/meetings";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(getAccessToken());
            
            Map<String, Object> body = new HashMap<>();
            body.put("topic", topic);
            body.put("type", 2); // Scheduled meeting
            body.put("start_time", startTime.format(DateTimeFormatter.ISO_DATE_TIME));
            body.put("duration", 60); // 1 hour
            body.put("settings", Map.of(
                "host_video", true,
                "participant_video", true,
                "join_before_host", true,
                "mute_upon_entry", true,
                "waiting_room", false,
                "auto_recording", "cloud"
            ));
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
            
            if (response.getStatusCode() != HttpStatus.CREATED && response.getStatusCode() != HttpStatus.OK) {
                logger.error("Failed to create Zoom meeting. Status code: {}", response.getStatusCode());
                throw new RuntimeException("Failed to create Zoom meeting");
            }
            
            Map<String, Object> meetingData = response.getBody();
            if (meetingData == null || !meetingData.containsKey("join_url")) {
                logger.error("Invalid response from Zoom API: {}", meetingData);
                throw new RuntimeException("Invalid response from Zoom API");
            }
            
            String joinUrl = (String) meetingData.get("join_url");
            String meetingId = String.valueOf(meetingData.get("id"));
            logger.info("Successfully created Zoom meeting with ID: {} and join URL: {}", meetingId, joinUrl);
            return joinUrl;
        } catch (Exception e) {
            logger.error("Error creating Zoom meeting: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create Zoom meeting: " + e.getMessage());
        }
    }

    @Override
    public String getRecordingUrl(String meetingId) {
        try {
            logger.info("Getting recording URL for meeting ID: {}", meetingId);
            String url = ZOOM_API_BASE_URL + "/meetings/" + meetingId + "/recordings";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(getAccessToken());
            
            HttpEntity<?> request = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
            
            if (response.getStatusCode() != HttpStatus.OK) {
                logger.error("Failed to get recording URL. Status code: {}", response.getStatusCode());
                throw new RuntimeException("Failed to get recording URL");
            }
            
            Map<String, Object> recordingData = response.getBody();
            if (recordingData == null || !recordingData.containsKey("download_url")) {
                logger.error("Invalid response from Zoom API: {}", recordingData);
                throw new RuntimeException("Invalid response from Zoom API");
            }
            
            String downloadUrl = (String) recordingData.get("download_url");
            logger.info("Successfully retrieved recording URL: {}", downloadUrl);
            return downloadUrl;
        } catch (Exception e) {
            logger.error("Error getting recording URL: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get recording URL: " + e.getMessage());
        }
    }

    private String getAccessToken() {
        try {
            logger.info("Getting Zoom access token");
            String url = "https://zoom.us/oauth/token";
            String credentials = Base64.getEncoder().encodeToString((apiKey + ":" + apiSecret).getBytes());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Authorization", "Basic " + credentials);
            
            String body = "grant_type=account_credentials&account_id=" + accountId;
            
            HttpEntity<String> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
            
            if (response.getStatusCode() != HttpStatus.OK) {
                logger.error("Failed to get access token. Status code: {}", response.getStatusCode());
                throw new RuntimeException("Failed to get access token");
            }
            
            Map<String, Object> tokenData = response.getBody();
            if (tokenData == null || !tokenData.containsKey("access_token")) {
                logger.error("Invalid response from Zoom API: {}", tokenData);
                throw new RuntimeException("Invalid response from Zoom API");
            }
            
            String token = (String) tokenData.get("access_token");
            logger.info("Successfully obtained Zoom access token");
            return token;
        } catch (Exception e) {
            logger.error("Error getting Zoom access token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get access token: " + e.getMessage());
        }
    }
} 