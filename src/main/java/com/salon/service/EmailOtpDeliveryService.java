package com.salon.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * EMAIL OTP DELIVERY SERVICE
 *
 * Implements OtpDeliveryService to send OTP via email.
 */
@Service("emailOtpDeliveryService")
public class EmailOtpDeliveryService implements OtpDeliveryService {

    private static final Logger logger = LoggerFactory.getLogger(EmailOtpDeliveryService.class);

    private final EmailService emailService;

    public EmailOtpDeliveryService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void deliver(String channel, String recipient, String otp) {
        try {
            emailService.sendOtpEmail(recipient, otp);
            logger.info("OTP sent via email to: {}", recipient);
        } catch (Exception e) {
            logger.error("Failed to send OTP via email to {}: {}", recipient, e.getMessage());
            throw new RuntimeException("Failed to send OTP via email", e);
        }
    }
}
