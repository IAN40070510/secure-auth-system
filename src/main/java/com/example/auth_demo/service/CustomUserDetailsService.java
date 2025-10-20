package com.example.auth_demo.service; // 確保套件名稱正確

import com.example.auth_demo.model.User;
import com.example.auth_demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service // <<-- 關鍵註解！讓 Spring 管理這個 Bean
public class CustomUserDetailsService implements UserDetailsService { // <<-- 實現 UserDetailsService 介面

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 使用 UserRepository 從資料庫中根據用戶名查找用戶
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // 2. 將我們自己的 User 物件，轉換成 Spring Security 需要的 UserDetails 物件
        //    - 第一个參數：用戶名
        //    - 第二个參數：從資料庫取出的、已經加密的密碼
        //    - 第三个參數：權限列表（我們暫時給一個空的）
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                new ArrayList<>() // 權限列表，進階功能會用到
        );
    }
}