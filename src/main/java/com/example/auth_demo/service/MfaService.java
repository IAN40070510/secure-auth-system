package com.example.auth_demo.service;

import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm; 
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Base64; // <--- 1. 新增這個 import

@Slf4j
@Service
public class MfaService {

    private final SecretGenerator secretGenerator;
    private final QrGenerator qrGenerator;
    private final CodeVerifier codeVerifier;

    public MfaService(SecretGenerator secretGenerator, QrGenerator qrGenerator, CodeVerifier codeVerifier) {
        this.secretGenerator = secretGenerator;
        this.qrGenerator = qrGenerator;
        this.codeVerifier = codeVerifier;
    }

    public String generateNewSecret() {
        return secretGenerator.generate();
    }

    public String generateQrCodeUri(String secret, String email, String issuer) {
        QrData data = new QrData.Builder()
                .label(email)
                .secret(secret)
                .issuer(issuer)
                .algorithm(HashingAlgorithm.SHA1)
                .digits(6)
                .period(30)
                .build();

        try {
            // 2. 修正：將 byte[] 轉換為 Base64 Data URI 字串
            byte[] imageData = qrGenerator.generate(data);
            String base64Image = Base64.getEncoder().encodeToString(imageData);
            
            // 回傳標準的 Data URI 格式
            return "data:image/png;base64," + base64Image;
            
        } catch (QrGenerationException e) {
            log.error("Failed to generate QR URI for email: {}", email, e);
            throw new RuntimeException("Error generating QR code URI", e);
        }
    }

    public boolean verifyCode(String secret, String code) {
        return codeVerifier.isValidCode(secret, code);
    }
}