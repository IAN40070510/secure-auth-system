package com.example.auth_demo.service;

import com.example.auth_demo.dto.RegisterRequest;
import com.example.auth_demo.model.User;
import com.example.auth_demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service // **確保有 @Service 註解**
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // **使用建構子注入，這是最佳實踐**
    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(RegisterRequest registerRequest) {
        // 檢查用戶名是否已存在
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new IllegalArgumentException("Error: Username is already taken!");
        }

        // 檢查電子郵件是否已存在
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Error: Email is already in use!");
        }

        // 建立新的使用者物件
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        // **使用注入的 passwordEncoder 來加密密碼**
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setMfaEnabled(false); // 預設禁用 MFA

        // 儲存到資料庫並返回
        return userRepository.save(user);
    }
}