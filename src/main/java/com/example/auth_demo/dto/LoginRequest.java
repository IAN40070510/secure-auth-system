package com.example.auth_demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "用戶名不得為空")
    private String username;

    @NotBlank(message = "密碼不得為空")
    private String password;
}