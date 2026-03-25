package com.salon.service;

import com.salon.config.OtpDeliveryProperties;
import com.salon.exception.BadRequestException;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class TwilioOtpDeliveryService implements OtpDeliveryService {

    private final OtpDeliveryProperties properties;

    public TwilioOtpDeliveryService(OtpDeliveryProperties properties) {
        this.properties = properties;
    }

    @Override
    public void deliver(String channel, String target, String otp) {
        if (!"PHONE".equalsIgnoreCase(channel)) {
            return;
        }
        OtpDeliveryProperties.Twilio twilio = properties.getTwilio();
        if (!twilio.isEnabled()) {
            return;
        }
        if (isBlank(twilio.getAccountSid()) || isBlank(twilio.getAuthToken()) || isBlank(twilio.getFromNumber())) {
            throw new BadRequestException("SMS OTP provider is not configured.");
        }

        Twilio.init(twilio.getAccountSid(), twilio.getAuthToken());
        String body = "Your SalonApp OTP is " + otp + ". It expires in 5 minutes.";
        Message.creator(new PhoneNumber(target), new PhoneNumber(twilio.getFromNumber()), body).create();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
