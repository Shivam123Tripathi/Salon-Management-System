package com.salon.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "otp")
public class OtpDeliveryProperties {
    private Twilio twilio = new Twilio();

    @Getter
    @Setter
    public static class Twilio {
        private boolean enabled;
        private String accountSid;
        private String authToken;
        private String fromNumber;
    }
}
