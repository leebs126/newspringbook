package com.springboot.ckb.common.security.jwt;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.springboot.ckb.common.security.domain.OauthMember;
import com.springboot.ckb.member.domain.Member;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TokenProvider {
	private final JwtProperties jwtProperties;
	
	public String generateToken(Member user, Duration expiredAt) {
			Date now =new Date();
			return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);
	}
	
	public String generateToken(OauthMember oauthUser, Duration expiredAt) {
		Date now =new Date();
		return makeToken(new Date(now.getTime() + expiredAt.toMillis()), oauthUser);
}
	
	
	/// ✅ 일반 회원용 토큰 생성
	private String makeToken(Date expiry, Member user) {
	    Date now = new Date();
	    Key key = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));

	    return Jwts.builder()
	            .setSubject(user.getEmail())  // 일반 회원은 email로 식별
	            .claim("type", "LOCAL")       // 토큰 타입 구분용 클레임
	            .claim("memId", user.getMemId())
	            .setIssuedAt(now)
	            .setExpiration(expiry)
	            .signWith(key, SignatureAlgorithm.HS256)
	            .compact();
	}

	// ✅ OAuth 회원용 토큰 생성
	private String makeToken(Date expiry, OauthMember oauthUser) {
	    Date now = new Date();
	    Key key = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));

	    return Jwts.builder()
	            .setSubject(oauthUser.getEmail()) // OAuth2 유저의 이메일
	            .claim("type", "OAUTH")           // 토큰 타입 구분용 클레임
	            .claim("provider", oauthUser.getProvider()) // Google, Naver 등
	            .setIssuedAt(now)
	            .setExpiration(expiry)
	            .signWith(key, SignatureAlgorithm.HS256)
	            .compact();
	}
	
	//2: JWT 토큰 유효성 검증 메서드
	public boolean validToken(String token) {
		try {
			// ✅ 1. Key 객체 생성 (생성 시점 통일)
			Key key = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));

			// ✅ 2. 최신 구문: parserBuilder() + setSigningKey(key) + build()
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token); // ✅ 실제 검증 (유효하면 예외 없음)

			return true;
		} catch (Exception e) {
			// 만료, 위조, 형식 오류 등 모든 예외는 false 처리
			return false;
		}
	}
	
	//3: 토큰 기반으로 인증 정보를 가져오는 메서드
	public Authentication getAuthentication(String token) {
		Claims claims = getClaims(token);
		Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
		return new UsernamePasswordAuthenticationToken(new org.springframework.security.core.userdetails.User
				(claims.getSubject(), "", authorities), token, authorities);
	}
	
	//4: 토큰 기반으로 유저 ID를 가져오는 메서드
	public Long getUserId(String token) {
		Claims claims = getClaims(token);
		return claims.get("id", Long.class);
	}
	
	private Claims getClaims(String token) {
	    // ✅ 1. Key 객체 생성 (generateToken()에서 사용한 것과 반드시 동일하게)
	    Key key = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));

	    // ✅ 2. 최신 문법: parserBuilder() → setSigningKey(key) → build() → parseClaimsJws()
	    return Jwts.parserBuilder()
	            .setSigningKey(key)
	            .build()
	            .parseClaimsJws(token)
	            .getBody(); // ✅ 클레임(Claims) 추출
	}
}











