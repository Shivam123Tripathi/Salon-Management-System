package com.salon.service;

public interface OtpDeliveryService {
    void deliver(String channel, String target, String otp);
}
