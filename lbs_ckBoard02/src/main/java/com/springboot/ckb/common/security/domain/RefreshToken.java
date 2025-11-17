package com.springboot.ckb.common.security.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class RefreshToken {

    private Long id;             // 시퀀스로 생성
    private String userId;         // 사용자 ID (unique)
    private String refreshToken; // 리프레시 토큰 값

    public RefreshToken(String userId, String refreshToken) {
        this.userId = userId;
        this.refreshToken = refreshToken;
    }

    public void update(String newRefreshToken) {
        this.refreshToken = newRefreshToken;
    }
}
