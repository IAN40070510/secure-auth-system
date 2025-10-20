package com.example.auth_demo.controller;

import com.example.auth_demo.dto.RegisterRequest;
import com.example.auth_demo.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // 註冊端點
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            authService.register(registerRequest);
            return ResponseEntity.ok("使用者註冊成功！");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 登入端點
    // 注意：Spring Security 的 formLogin 會自動處理 /login 的 POST 請求。
    // 這個方法主要用於示意，並在成功登入後返回一個友善的訊息。
    // 實際的認證流程由 SecurityFilterChain 中的 `formLogin()` 處理。
    @PostMapping("/login-success")
    public ResponseEntity<?> loginSuccess() {
        // 當 Spring Security 成功認證後，可以重定向到此端點或由前端處理
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok("使用者 '" + username + "' 登入成功！");
    }

    // 登出端點
    // `SecurityConfig` 中已配置了 /api/auth/logout，Spring Security 會自動處理。
    // 這個方法是可選的，如果需要自訂邏輯可以實現。
    // 這裡我們讓 Spring Security 自動處理 session 失效。
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("登出成功！");
    }
}