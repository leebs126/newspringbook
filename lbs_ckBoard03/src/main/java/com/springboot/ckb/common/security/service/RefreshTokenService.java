package com.springboot.ckb.common.security.service;

import org.springframework.stereotype.Service;

import com.springboot.ckb.common.security.domain.RefreshToken;
import com.springboot.ckb.common.security.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
	private final RefreshTokenRepository refreshTokenRepository;
	
	public RefreshToken findByRefreshToken(String refreshToken) {
		return refreshTokenRepository.findByRefreshToken(refreshToken)
				.orElseThrow(() -> new IllegalArgumentException("Unexpected user!"));
	}
}
