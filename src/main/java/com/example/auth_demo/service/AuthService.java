package com.example.auth_demo.service;

import com.example.auth_demo.dto.RegisterRequest;
import com.example.auth_demo.model.User;
import com.example.auth_demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void register(RegisterRequest registerRequest) {
        // 安全性檢查：確認用戶名是否已被使用
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new IllegalArgumentException("錯誤：該用戶名已被註冊！");
        }

        // 安全性檢查：確認 Email 是否已被使用
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("錯誤：該 Email 已被註冊！");
        }

        // 建立新的使用者實體
        User user = new User(
            registerRequest.getUsername(),
            registerRequest.getEmail(),
            // 對密碼進行 BCrypt 哈希加密
            passwordEncoder.encode(registerRequest.getPassword())
        );

        userRepository.save(user);
    }
}