package com.springboot.ckb.common.security.service;

import java.time.Duration;

import org.springframework.stereotype.Service;

import com.springboot.ckb.common.security.jwt.TokenProvider;
import com.springboot.ckb.member.domain.Member;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TokenService {
	private final TokenProvider tokenProvider;
	private final RefreshTokenService refreshTokenService;
	private final MemService memService;
	
	public String createNewAccessToken(String refreshToken) {
		//토큰 유효성 검사에 실패하면 얘외 발생
		if(!tokenProvider.validToken(refreshToken)) {
			throw new IllegalArgumentException("Unexpected user!");
		}
		
		String userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
		Member user = memService.findById(userId);
		
		return tokenProvider.generateToken(user, Duration.ofHours(2));
	}
}
