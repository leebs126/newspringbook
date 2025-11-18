package com.springboot.ckb.admin.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;

@Component
public class AdminCodeStore {

    private final Map<String, AuthCode> store = new ConcurrentHashMap<>();

    @Data
    @AllArgsConstructor
    public static class AuthCode {
        private String code;      // 인증코드
        private long expiresAt;   // 만료 시간(타임스탬프)
    }

    public void save(String email, String code) {
        store.put(email, new AuthCode(code, System.currentTimeMillis() + 3 * 60 * 1000));
    }

    public AuthCode get(String email) {
        return store.get(email);
    }

    public void remove(String email) {
        store.remove(email);
    }
}
