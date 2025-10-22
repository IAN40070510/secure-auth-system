package com.example.auth_demo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails; // <-- 新增 import

import java.util.Collection; // <-- 新增 import
import java.util.Collections; // <-- 新增 import

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails { // <-- 關鍵修改 1：實現 UserDetails

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "mfa_secret")
    private String mfaSecret;

    @Column(name = "mfa_enabled", nullable = false)
    private boolean mfaEnabled = false;

    // --- 你的建構子被完整保留下來了 ---
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // --- 關鍵修改 2：補上 UserDetails 介面需要的方法 ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}