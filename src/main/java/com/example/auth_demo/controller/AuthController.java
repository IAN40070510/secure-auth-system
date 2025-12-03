package com.example.auth_demo.controller;

import com.example.auth_demo.dto.LoginRequest;
import com.example.auth_demo.dto.MfaVerificationRequest;
import com.example.auth_demo.dto.RegisterRequest;
import com.example.auth_demo.model.User;
import com.example.auth_demo.repository.UserRepository;
import com.example.auth_demo.service.AuthService;
import com.example.auth_demo.service.MfaService;
import jakarta.servlet.http.HttpServletRequest; // <--- 注意這個
import jakarta.servlet.http.HttpServletResponse; // <--- 注意這個
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext; // <--- 新增
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository; // <--- 關鍵類別
import org.springframework.security.web.context.SecurityContextRepository; // <--- 關鍵介面
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final MfaService mfaService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    // 👇 1. 定義 SecurityContextRepository (這是 Spring Security 6 手動存 Session 的工具)
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    public static final String MFA_PENDING_USERNAME = "MFA_PENDING_USERNAME";

    @Autowired
    public AuthController(AuthService authService, MfaService mfaService, AuthenticationManager authenticationManager, UserRepository userRepository) {
        this.authService = authService;
        this.mfaService = mfaService;
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

    // 👇 2. 修改登入方法，加入 HttpServletRequest 和 HttpServletResponse
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, 
                                              HttpServletRequest request, 
                                              HttpServletResponse response) { // <--- 參數需加入 request, response
        
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        if (user.isMfaEnabled()) {
            // Case A: 需要 MFA
            HttpSession session = request.getSession(true); // 確保 Session 存在
            session.setAttribute(MFA_PENDING_USERNAME, user.getUsername());
            
            Map<String, String> result = new HashMap<>();
            result.put("status", "MFA_REQUIRED");
            return ResponseEntity.ok(result);
            
        } else {
            // Case B: 直接登入 (最關鍵的修改在這裡！！！)
            
            // 1. 建立空的 Context
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            // 2. 放入認證資訊
            context.setAuthentication(authentication);
            // 3. 設定到 Holder
            SecurityContextHolder.setContext(context);
            
            // 👇👇👇 4. 強制儲存 Context 到 Session (解決 403 的核心) 👇👇👇
            securityContextRepository.saveContext(context, request, response);

            return ResponseEntity.ok(user);
        }
    }
    
    // 👇 3. 修改 MFA 驗證，同樣需要手動儲存
    @PostMapping("/verify-mfa")
    public ResponseEntity<?> verifyMfa(@Valid @RequestBody MfaVerificationRequest req, 
                                       HttpServletRequest request, 
                                       HttpServletResponse response) {
        
        HttpSession session = request.getSession(false);
        if (session == null) return ResponseEntity.badRequest().body("Session expired");

        String pendingUsername = (String) session.getAttribute(MFA_PENDING_USERNAME);
        
        if (pendingUsername == null || !pendingUsername.equals(req.getUsername())) {
            return ResponseEntity.badRequest().body("MFA pending state not found");
        }
        
        User user = userRepository.findByUsername(pendingUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        boolean isValid = mfaService.verifyCode(user.getMfaSecret(), req.getMfaCode());
        
        if (isValid) {
            session.removeAttribute(MFA_PENDING_USERNAME);

            // 手動建立認證物件
            Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            
            // 👇👇👇 強制儲存登入狀態 👇👇👇
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, request, response);
            
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.badRequest().body("Invalid MFA code.");
        }
    }
}