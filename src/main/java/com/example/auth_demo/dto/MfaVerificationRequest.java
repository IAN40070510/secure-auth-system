package com.example.auth_demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MfaVerificationRequest {
    
    @NotBlank(message = "Username cannot be empty")
    private String username;

    @NotBlank(message = "MFA code cannot be empty")
    @Size(min = 6, max = 6, message = "MFA code must be 6 digits")
    private String mfaCode;
}