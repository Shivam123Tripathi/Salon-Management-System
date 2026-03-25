package com.salon.service;

import com.salon.exception.BadRequestException;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    private static final long OTP_TTL_SECONDS = 300; // 5 minutes
    private static final String DEMO_OTP = "123456"; // Fixed OTP for demo
    private static final boolean DEMO_MODE = true; // Set to false for production

    private static final SecureRandom RANDOM = new SecureRandom();
    private final Map<String, OtpRecord> otpStore = new ConcurrentHashMap<>();

    public String issueOtp(String channel, String target) {
        String normalizedChannel = normalizeChannel(channel);
        String normalizedTarget = normalizeTarget(target);
        String key = key(normalizedChannel, normalizedTarget);

        // DEMO MODE: Use fixed OTP "123456" for easy testing
        String otp = DEMO_MODE ? DEMO_OTP : String.format("%06d", RANDOM.nextInt(1_000_000));
        otpStore.put(key, new OtpRecord(otp, Instant.now().plusSeconds(OTP_TTL_SECONDS)));

        // Always log OTP to console for demo
        System.out.println("\n");
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║         📱 DEMO MODE - USE THIS OTP              ║");
        System.out.println("╠══════════════════════════════════════════════════╣");
        System.out.println("║                                                  ║");
        System.out.println("║     OTP CODE:  " + otp + "                           ║");
        System.out.println("║                                                  ║");
        System.out.println("║  For: " + String.format("%-41s", normalizedTarget) + " ║");
        System.out.println("║  Via: " + String.format("%-41s", normalizedChannel) + " ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println("\n");

        return otp;
    }

    public void verifyOtp(String channel, String target, String otp) {
        String normalizedChannel = normalizeChannel(channel);
        String normalizedTarget = normalizeTarget(target);
        String key = key(normalizedChannel, normalizedTarget);

        // DEMO MODE: Accept the demo OTP without checking storage
        if (DEMO_MODE && DEMO_OTP.equals(otp)) {
            System.out.println("✅ OTP verified successfully (DEMO MODE)");
            otpStore.remove(key);
            return;
        }

        OtpRecord record = otpStore.get(key);
        if (record == null) {
            throw new BadRequestException("OTP not requested for this target");
        }
        if (Instant.now().isAfter(record.expiresAt())) {
            otpStore.remove(key);
            throw new BadRequestException("OTP expired. Please request a new OTP.");
        }
        if (!record.code().equals(otp)) {
            throw new BadRequestException("Invalid OTP");
        }
        otpStore.remove(key);
        System.out.println("✅ OTP verified successfully");
    }

    private String normalizeChannel(String channel) {
        if (channel == null) {
            throw new BadRequestException("OTP channel is required");
        }
        String normalized = channel.trim().toUpperCase();
        if (!"EMAIL".equals(normalized) && !"PHONE".equals(normalized)) {
            throw new BadRequestException("OTP channel must be EMAIL or PHONE");
        }
        return normalized;
    }

    private String normalizeTarget(String target) {
        if (target == null || target.trim().isEmpty()) {
            throw new BadRequestException("OTP target is required");
        }
        return target.trim().toLowerCase();
    }

    private String key(String channel, String target) {
        return channel + "::" + target;
    }

    private record OtpRecord(String code, Instant expiresAt) { }
}
