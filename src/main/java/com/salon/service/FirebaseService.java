package com.salon.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * FIREBASE CLOUD MESSAGING SERVICE
 *
 * Handles push notifications to mobile devices using Firebase.
 */
@Service
public class FirebaseService {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseService.class);

    @Value("${firebase.enabled:false}")
    private boolean enabled;

    @Value("${firebase.config.path:}")
    private String configPath;

    @PostConstruct
    public void initialize() {
        if (!enabled) {
            logger.info("Firebase is disabled. Push notifications will not be sent.");
            return;
        }

        try {
            if (configPath == null || configPath.isEmpty()) {
                logger.warn("Firebase config path not set. Push notifications disabled.");
                return;
            }

            FileInputStream serviceAccount = new FileInputStream(configPath);

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                logger.info("Firebase initialized successfully");
            }

        } catch (IOException e) {
            logger.error("Failed to initialize Firebase: {}", e.getMessage());
        }
    }

    /**
     * SEND PUSH NOTIFICATION TO SINGLE DEVICE
     */
    public void sendNotification(String deviceToken, String title, String body, Map<String, String> data) {
        if (!enabled || deviceToken == null) {
            logger.debug("Notification skipped (Firebase disabled or no token): {}", title);
            return;
        }

        try {
            Message message = Message.builder()
                    .setToken(deviceToken)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .putAllData(data)
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            logger.info("Push notification sent successfully: {}", response);

        } catch (FirebaseMessagingException e) {
            logger.error("Failed to send push notification: {}", e.getMessage());
        }
    }

    /**
     * SEND BOOKING CONFIRMATION NOTIFICATION
     */
    public void sendBookingConfirmation(String deviceToken, String serviceName, String date, String time) {
        Map<String, String> data = new HashMap<>();
        data.put("type", "BOOKING_CONFIRMATION");
        data.put("service", serviceName);
        data.put("date", date);
        data.put("time", time);

        sendNotification(
                deviceToken,
                "Booking Confirmed!",
                "Your " + serviceName + " appointment is confirmed for " + date + " at " + time,
                data
        );
    }

    /**
     * SEND APPOINTMENT REMINDER NOTIFICATION
     */
    public void sendAppointmentReminder(String deviceToken, String serviceName, String time) {
        Map<String, String> data = new HashMap<>();
        data.put("type", "APPOINTMENT_REMINDER");
        data.put("service", serviceName);

        sendNotification(
                deviceToken,
                "Upcoming Appointment",
                "Reminder: Your " + serviceName + " appointment is at " + time + " today",
                data
        );
    }

    /**
     * SEND CANCELLATION NOTIFICATION
     */
    public void sendCancellationNotification(String deviceToken, String serviceName) {
        Map<String, String> data = new HashMap<>();
        data.put("type", "APPOINTMENT_CANCELLED");
        data.put("service", serviceName);

        sendNotification(
                deviceToken,
                "Appointment Cancelled",
                "Your " + serviceName + " appointment has been cancelled",
                data
        );
    }
}
