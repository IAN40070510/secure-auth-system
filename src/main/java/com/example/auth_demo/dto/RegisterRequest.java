package com.example.auth_demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "用戶名不得為空")
    @Size(min = 3, max = 20, message = "用戶名長度必須在 3 到 20 個字元之間")
    private String username;

    @NotBlank(message = "Email 不得為空")
    @Email(message = "Email 格式不正確")
    @Size(max = 50)
    private String email;

    @NotBlank(message = "密碼不得為空")
    @Size(min = 6, max = 40, message = "密碼長度必須在 6 到 40 個字元之間")
    private String password;
}