package com.example.auth_demo.controller;

import com.example.auth_demo.dto.MfaVerificationRequest;
import com.example.auth_demo.model.User;
import com.example.auth_demo.repository.UserRepository;
import com.example.auth_demo.service.MfaService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*; // 確保包含 GetMapping

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final MfaService mfaService;
    private final UserRepository userRepository;
    
    // 應用程式名稱，用於顯示在 Google Authenticator
    @Value("${spring.application.name:AuthDemo}")
    private String applicationName;

    // Session Key 常量
    public static final String MFA_SETUP_SECRET = "MFA_SETUP_SECRET";

    public UserController(MfaService mfaService, UserRepository userRepository) {
        this.mfaService = mfaService;
        this.userRepository = userRepository;
    }
    
    // 內部輔助方法：獲取當前登入的使用者資訊
    private Optional<User> getCurrentUser(Authentication authentication) {
        if (authentication == null) return Optional.empty();
        String username = authentication.getName();
        return userRepository.findByUsername(username);
    }

    /**
     * 1. 產生 MFA Secret Key
     * 2. 將 Secret Key 暫存到 Session 中
     * 3. 產生 QR Code URI 回傳給前端
     */
    @PostMapping("/mfa/setup")
    public ResponseEntity<?> setupMfa(Authentication authentication, HttpSession session) {
        User user = getCurrentUser(authentication)
                .orElseThrow(() -> new UsernameNotFoundException("User not found in session"));

        if (user.isMfaEnabled()) {
            return ResponseEntity.badRequest().body("MFA is already enabled.");
        }

        // 1. 產生新的 Secret
        String newSecret = mfaService.generateNewSecret();
        
        // 2. 暫存 Secret 到 Session。**注意：這個 Secret 尚未存入資料庫**
        session.setAttribute(MFA_SETUP_SECRET, newSecret);

        // 3. 產生 QR Code URI
        String qrUri = mfaService.generateQrCodeUri(newSecret, user.getEmail(), applicationName);

        // 回傳 QR URI，前端用來顯示 QR Code
        Map<String, String> response = new HashMap<>();
        response.put("qrUri", qrUri);
        response.put("secret", newSecret); 
        return ResponseEntity.ok(response);
    }

    /**
     * 1. 驗證使用者提交的驗證碼 (與 Session 中的暫存 Secret 比對)
     * 2. 驗證成功後，將 Secret 存入資料庫，並將 mfaEnabled 設為 true
     */
    @PostMapping("/mfa/confirm")
    public ResponseEntity<?> confirmMfa(@Valid @RequestBody MfaVerificationRequest request, 
                                        Authentication authentication, 
                                        HttpSession session) {
        
        // 1. 從 Session 中取出暫存的 Secret
        String tempSecret = (String) session.getAttribute(MFA_SETUP_SECRET);

        if (tempSecret == null) {
            return ResponseEntity.badRequest().body("MFA setup process not started or session expired.");
        }

        // 2. 驗證使用者輸入的 MFA Code
        boolean isValid = mfaService.verifyCode(tempSecret, request.getMfaCode());

        if (isValid) {
            User user = getCurrentUser(authentication)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found in session"));

            // 3. 驗證成功：將 Secret 存入 DB，並啟用 MFA
            user.setMfaSecret(tempSecret);
            user.setMfaEnabled(true);
            userRepository.save(user);

            // 4. 清除 Session 中的暫存 Secret
            session.removeAttribute(MFA_SETUP_SECRET);
            
            return ResponseEntity.ok("MFA enabled successfully!");
        } else {
            return ResponseEntity.badRequest().body("Invalid MFA code.");
        }
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> checkSession(Authentication authentication) {
        return getCurrentUser(authentication)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(401).build()); // 若無 Session 則回傳 401
    }

    @PostMapping("/mfa/disable")
    public ResponseEntity<?> disableMfa(Authentication authentication) {
        User user = getCurrentUser(authentication)
                .orElseThrow(() -> new UsernameNotFoundException("User not found in session"));

        // 清除 MFA 設定
        user.setMfaEnabled(false);
        user.setMfaSecret(null); // 建議清除 Secret，下次啟用時重新產生
        
        userRepository.save(user);

        return ResponseEntity.ok("MFA has been disabled.");
    }

}