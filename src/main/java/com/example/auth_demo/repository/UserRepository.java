package com.example.auth_demo.repository;

import com.example.auth_demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 根據用戶名查找用戶，用於登入和註冊時的檢查
    Optional<User> findByUsername(String username);

    // 檢查用戶名是否存在
    Boolean existsByUsername(String username);

    // 檢查 Email 是否存在
    Boolean existsByEmail(String email);
}