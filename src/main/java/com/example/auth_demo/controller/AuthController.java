package com.example.auth_demo.controller;

import com.example.auth_demo.dto.LoginRequest; // 引入 LoginRequest
import com.example.auth_demo.dto.RegisterRequest;
import com.example.auth_demo.model.User;
import com.example.auth_demo.repository.UserRepository;
import com.example.auth_demo.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager; // 引入
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // 引入
import org.springframework.security.core.Authentication; // 引入
import org.springframework.security.core.context.SecurityContextHolder; // 引入
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager; // **注入 AuthenticationManager**
    private final UserRepository userRepository; // 注入 UserRepository 以便獲取用戶資訊

    @Autowired
    public AuthController(AuthService authService, AuthenticationManager authenticationManager, UserRepository userRepository) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            authService.register(registerRequest);
            return ResponseEntity.ok("User registered successfully!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // --- 👇 新增的登入端點 ---
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        // 1. 使用 AuthenticationManager 進行認證
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        // 2. 如果認證成功，將認證資訊存入 SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. 從資料庫中查找使用者，以回傳不含密碼的安全資訊
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // **重要：成功登入後，Spring Security 會自動處理 Session 和 Cookie**
        // 我們回傳使用者資訊給前端，讓 Pinia store 可以更新
        return ResponseEntity.ok(user);
    }
    
}