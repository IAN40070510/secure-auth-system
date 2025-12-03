package com.example.auth_demo.config;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TotpConfig {

    // 1. 設定 SecretGenerator (用於產生密鑰)
    @Bean
    public SecretGenerator secretGenerator() {
        return new DefaultSecretGenerator();
    }

    // 2. 設定 QrGenerator (用於產生 QR Code 圖片)
    @Bean
    public QrGenerator qrGenerator() {
        // 使用 Zxing 庫產生 PNG
        return new ZxingPngQrGenerator();
    }

    // 3. 設定 CodeVerifier (用於驗證前端傳來的 6 位數代碼)
    @Bean
    public CodeVerifier codeVerifier() {
        // 設定時間提供者 (使用系統時間)
        TimeProvider timeProvider = new SystemTimeProvider();
        
        // 設定代碼生成規則 (SHA1 演算法, 6 位數) - 這是 Google Authenticator 的標準
        CodeGenerator codeGenerator = new DefaultCodeGenerator(HashingAlgorithm.SHA1, 6);
        
        return new DefaultCodeVerifier(codeGenerator, timeProvider);
    }
}