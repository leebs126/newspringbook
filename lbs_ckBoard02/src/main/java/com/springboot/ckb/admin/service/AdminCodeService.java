package com.springboot.ckb.admin.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AdminCodeService {

    // 메모리 저장소 (동시성 안전)
    private final Map<String, AuthCode> codeStore = new ConcurrentHashMap<>();

    // 인증코드 객체
    private static class AuthCode {
        private String code;
        private long expiresAt; // 만료 시각 (밀리초)

        public AuthCode(String code, long expiresAt) {
            this.code = code;
            this.expiresAt = expiresAt;
        }

        public String getCode() {
            return code;
        }

        public long getExpiresAt() {
            return expiresAt;
        }
    }

    // 인증코드 생성 후 저장 (3분 유효)
    public String createCode(String email) {
        String code = generateCode();
        long expiresAt = System.currentTimeMillis() + 3 * 60 * 1000; // 3분 후 만료
        codeStore.put(email, new AuthCode(code, expiresAt));
        return code;
    }

    // 인증코드 검증
    public boolean verifyCode(String email, String inputCode) {
        AuthCode auth = codeStore.get(email);

        if (auth == null) return false; // 코드 없음

        // 3분 만료 확인
        if (System.currentTimeMillis() > auth.getExpiresAt()) {
            codeStore.remove(email);
            return false; // 만료
        }

        boolean match = auth.getCode().equals(inputCode);

        // 성공/실패 관계없이 코드 즉시 삭제
        codeStore.remove(email);

        return match;
    }

    // 랜덤 6자리 코드 생성
    private String generateCode() {
        return String.format("%06d", (int)(Math.random() * 900000));
    }
}
