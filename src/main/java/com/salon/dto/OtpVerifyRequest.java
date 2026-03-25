package com.salon.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OtpVerifyRequest {
    @NotBlank(message = "Channel is required")
    private String channel;

    @NotBlank(message = "Target is required")
    private String target;

    @NotBlank(message = "OTP is required")
    private String otp;
}
