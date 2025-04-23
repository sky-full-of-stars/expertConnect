package com.uci.expertConnect.service.impl;

import com.uci.expertConnect.model.Meeting;
import com.uci.expertConnect.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Override
    public void sendMeetingLink(Meeting meeting) {
        // Send to expert
        sendEmail(
            meeting.getExpert().getUser().getEmail(),
            "New Meeting Scheduled",
            String.format(
                "A new meeting has been scheduled with %s on %s.\n\n" +
                "Join URL: %s",
                meeting.getUser().getName(),
                meeting.getMeetingDateTime(),
                meeting.getZoomJoinUrl()
            )
        );

        // Send to user
        sendEmail(
            meeting.getUser().getEmail(),
            "New Meeting Scheduled",
            String.format(
                "A new meeting has been scheduled with %s on %s.\n\n" +
                "Join URL: %s",
                meeting.getExpert().getUser().getName(),
                meeting.getMeetingDateTime(),
                meeting.getZoomJoinUrl()
            )
        );
    }

    @Override
    public void sendRecordingLink(Meeting meeting) {
        // Send to expert
        sendEmail(
            meeting.getExpert().getUser().getEmail(),
            "Meeting Recording Available",
            String.format(
                "The recording for your meeting with %s is now available.\n\n" +
                "Recording URL: %s",
                meeting.getUser().getName(),
                meeting.getRecordingUrl()
            )
        );

        // Send to user
        sendEmail(
            meeting.getUser().getEmail(),
            "Meeting Recording Available",
            String.format(
                "The recording for your meeting with %s is now available.\n\n" +
                "Recording URL: %s",
                meeting.getExpert().getUser().getName(),
                meeting.getRecordingUrl()
            )
        );
    }

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }
} 