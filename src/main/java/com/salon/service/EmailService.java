package com.salon.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * EMAIL SERVICE
 *
 * Handles all email communication including:
 * - OTP delivery
 * - Booking confirmations
 * - Appointment reminders
 * - Cancellation notifications
 */
@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${mail.from.name:Salon Management System}")
    private String fromName;

    @Value("${mail.from.address:noreply@salon.com}")
    private String fromAddress;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * SEND OTP EMAIL
     */
    public void sendOtpEmail(String toEmail, String otp) {
        String subject = "Your OTP Code - Salon Management System";
        String htmlContent = buildOtpEmailTemplate(otp);
        sendHtmlEmail(toEmail, subject, htmlContent);
    }

    /**
     * SEND BOOKING CONFIRMATION EMAIL
     */
    public void sendBookingConfirmation(
            String toEmail,
            String customerName,
            String serviceName,
            String artistName,
            LocalDate date,
            LocalTime startTime,
            String appointmentId) {

        String subject = "Booking Confirmed - " + serviceName;
        String htmlContent = buildBookingConfirmationTemplate(
                customerName, serviceName, artistName, date, startTime, appointmentId);
        sendHtmlEmail(toEmail, subject, htmlContent);
    }

    /**
     * SEND APPOINTMENT REMINDER EMAIL
     */
    public void sendAppointmentReminder(
            String toEmail,
            String customerName,
            String serviceName,
            String artistName,
            LocalDate date,
            LocalTime startTime) {

        String subject = "Reminder: Your Appointment Tomorrow";
        String htmlContent = buildReminderTemplate(
                customerName, serviceName, artistName, date, startTime);
        sendHtmlEmail(toEmail, subject, htmlContent);
    }

    /**
     * SEND CANCELLATION EMAIL
     */
    public void sendCancellationNotification(
            String toEmail,
            String customerName,
            String serviceName,
            LocalDate date,
            LocalTime startTime) {

        String subject = "Appointment Cancelled";
        String htmlContent = buildCancellationTemplate(
                customerName, serviceName, date, startTime);
        sendHtmlEmail(toEmail, subject, htmlContent);
    }

    /**
     * CORE EMAIL SENDING LOGIC
     */
    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromAddress, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("Email sent successfully to: {}", to);

        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            logger.error("Failed to send email to {}: {}", to, e.getMessage());
            // Don't throw exception - we don't want email failure to block the main flow
        }
    }

    // ======================== EMAIL TEMPLATES ========================

    private String buildOtpEmailTemplate(String otp) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px; }
                    .container { max-width: 600px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; }
                    .otp-box { background: #FFD700; color: #000; font-size: 32px; font-weight: bold;
                               text-align: center; padding: 20px; border-radius: 8px; letter-spacing: 8px; margin: 20px 0; }
                    h1 { color: #333; }
                    p { color: #666; line-height: 1.6; }
                    .footer { margin-top: 30px; padding-top: 20px; border-top: 1px solid #ddd; color: #999; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>🔐 Your OTP Code</h1>
                    <p>Hello,</p>
                    <p>Use the following One-Time Password (OTP) to complete your verification:</p>
                    <div class="otp-box">%s</div>
                    <p>This OTP is valid for <strong>10 minutes</strong> only.</p>
                    <p>If you didn't request this OTP, please ignore this email.</p>
                    <div class="footer">
                        <p>© 2024 Salon Management System. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(otp);
    }

    private String buildBookingConfirmationTemplate(
            String customerName, String serviceName, String artistName,
            LocalDate date, LocalTime startTime, String appointmentId) {

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px; }
                    .container { max-width: 600px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; }
                    .header { background: linear-gradient(135deg, #FFD700, #FFA500); padding: 20px; border-radius: 8px; text-align: center; }
                    .header h1 { color: #000; margin: 0; }
                    .details { background: #f9f9f9; padding: 20px; border-radius: 8px; margin: 20px 0; }
                    .detail-row { display: flex; margin: 10px 0; }
                    .detail-label { font-weight: bold; width: 150px; color: #333; }
                    .detail-value { color: #666; }
                    .button { background: #FFD700; color: #000; padding: 12px 30px; text-decoration: none;
                             border-radius: 6px; display: inline-block; font-weight: bold; margin: 20px 0; }
                    .footer { margin-top: 30px; padding-top: 20px; border-top: 1px solid #ddd; color: #999; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>✅ Booking Confirmed!</h1>
                    </div>
                    <p>Hi <strong>%s</strong>,</p>
                    <p>Great news! Your appointment has been successfully booked.</p>
                    <div class="details">
                        <div class="detail-row">
                            <div class="detail-label">Service:</div>
                            <div class="detail-value">%s</div>
                        </div>
                        <div class="detail-row">
                            <div class="detail-label">Artist:</div>
                            <div class="detail-value">%s</div>
                        </div>
                        <div class="detail-row">
                            <div class="detail-label">Date:</div>
                            <div class="detail-value">%s</div>
                        </div>
                        <div class="detail-row">
                            <div class="detail-label">Time:</div>
                            <div class="detail-value">%s</div>
                        </div>
                        <div class="detail-row">
                            <div class="detail-label">Booking ID:</div>
                            <div class="detail-value">%s</div>
                        </div>
                    </div>
                    <p>Please arrive 5 minutes before your scheduled time.</p>
                    <p>Need to reschedule or cancel? Login to your account to manage your bookings.</p>
                    <div class="footer">
                        <p>© 2024 Salon Management System. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                customerName,
                serviceName,
                artistName,
                date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")),
                startTime.format(DateTimeFormatter.ofPattern("hh:mm a")),
                appointmentId
            );
    }

    private String buildReminderTemplate(
            String customerName, String serviceName, String artistName,
            LocalDate date, LocalTime startTime) {

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px; }
                    .container { max-width: 600px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; }
                    h1 { color: #FFD700; }
                    .reminder-box { background: #FFF9E6; border-left: 4px solid #FFD700; padding: 15px; margin: 20px 0; }
                    .footer { margin-top: 30px; padding-top: 20px; border-top: 1px solid #ddd; color: #999; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>⏰ Appointment Reminder</h1>
                    <p>Hi <strong>%s</strong>,</p>
                    <p>This is a friendly reminder about your upcoming appointment:</p>
                    <div class="reminder-box">
                        <p><strong>Service:</strong> %s</p>
                        <p><strong>Artist:</strong> %s</p>
                        <p><strong>Date:</strong> %s</p>
                        <p><strong>Time:</strong> %s</p>
                    </div>
                    <p>We look forward to serving you! Please arrive 5 minutes early.</p>
                    <div class="footer">
                        <p>© 2024 Salon Management System. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                customerName,
                serviceName,
                artistName,
                date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")),
                startTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
            );
    }

    private String buildCancellationTemplate(
            String customerName, String serviceName, LocalDate date, LocalTime startTime) {

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px; }
                    .container { max-width: 600px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; }
                    h1 { color: #FF6B6B; }
                    .footer { margin-top: 30px; padding-top: 20px; border-top: 1px solid #ddd; color: #999; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>❌ Appointment Cancelled</h1>
                    <p>Hi <strong>%s</strong>,</p>
                    <p>Your appointment has been cancelled:</p>
                    <p><strong>Service:</strong> %s<br>
                       <strong>Date:</strong> %s<br>
                       <strong>Time:</strong> %s</p>
                    <p>If you didn't cancel this appointment, please contact us immediately.</p>
                    <p>We hope to see you again soon!</p>
                    <div class="footer">
                        <p>© 2024 Salon Management System. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                customerName,
                serviceName,
                date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")),
                startTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
            );
    }
}
