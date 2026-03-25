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
public class OtpRequest {
    @NotBlank(message = "Channel is required")
    private String channel; // EMAIL or PHONE

    @NotBlank(message = "Target is required")
    private String target; // email value or phone value
}
