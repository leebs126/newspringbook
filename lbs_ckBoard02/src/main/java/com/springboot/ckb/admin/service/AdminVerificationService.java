package com.springboot.ckb.admin.service;

import java.util.Random;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminVerificationService {

    private final AdminCodeStore codeStore;
    private final EmailService emailService;

    public void sendAuthCode(String email) {

        // 1) 랜덤 인증코드 생성
        String code = generateCode();

        // 2) 저장 (3분 유효)
        codeStore.save(email, code);

        // 3) 이메일 전송
        emailService.sendAdminVerificationCode(
                email,
                "관리자 계정 생성 인증코드",
                "인증코드: " + code + "\n3분 안에 입력해주세요."
        );
    }

    public boolean verifyCode(String email, String inputCode) {
        AdminCodeStore.AuthCode auth = codeStore.get(email);

        if (auth == null) return false;

        // 1) 만료 시간 확인
        if (System.currentTimeMillis() > auth.getExpiresAt()) {
            codeStore.remove(email);
            return false;
        }

        // 2) 코드 일치 확인
        boolean match = auth.getCode().equals(inputCode);

        // 3) 성공/실패와 무관하게 즉시 제거 (1회용)
        codeStore.remove(email);

        return match;
    }

    private String generateCode() {
        return String.format("%06d", new Random().nextInt(999999));  // 6자리 숫자
    }
}
