package com.example.auth_demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 建立一個 BCryptPasswordEncoder Bean，用於密碼加密
    // BCrypt 是目前公認的強度最高的哈希算法之一，它會自動加鹽
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF 保護預設是啟用的，對於瀏覽器客戶端是必要的安全措施
            // 如果你的 API 客戶端是 Postman 或其他非瀏覽器應用，測試時可以暫時禁用
            // .csrf(csrf -> csrf.disable())
            // 配置 HTTP 請求授權規則
            .authorizeHttpRequests(authorize -> authorize
                // 允許對 /api/auth/ 下的所有請求公開存取 (註冊、登入)
                .requestMatchers("/api/auth/**").permitAll()
                // 要求對 /api/user/ 下的所有請求必須經過身份驗證
                .requestMatchers("/api/user/**").authenticated()
                // 其他任何請求都需要身份驗證
                .anyRequest().authenticated()
            )
            // 啟用並配置預設的登入頁面，Spring Security 會自動處理 /login 端點
            .formLogin(withDefaults())
            // 啟用並配置登出功能
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout") // 指定登出 URL
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setStatus(200); // 登出成功後回傳 200 OK
                })
            );

            

        return http.build();
    }
}