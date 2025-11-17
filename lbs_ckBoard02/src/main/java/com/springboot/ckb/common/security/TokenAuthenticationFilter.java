package com.springboot.ckb.common.security;

import java.io.IOException;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.springboot.ckb.common.security.jwt.TokenProvider;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//@RequiredArgsConstructor
@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {
	private final TokenProvider  tokenProvider;
	private final static String HEADER_AUTHENTICATION ="Authentication";
	private final static String TOKEN_PREFIX = "Bearer";
	
	public TokenAuthenticationFilter(@Lazy TokenProvider tokenProvider){
		this.tokenProvider = tokenProvider;
	}
			

	@Override
	protected void doFilterInternal(HttpServletRequest request, 
			HttpServletResponse response, 
			FilterChain filterChain)
			throws ServletException, IOException {
		
		//요청 헤더의 Authentication 키의 값 조회
		String authorizationHeader = request.getHeader(HEADER_AUTHENTICATION);
		//가져온 값에서 접두사 제거
		String token = getAccessToken(authorizationHeader);
		//가져온 토큰이 유효한지 확인하고, 유효한 때는 인증 정보 설정
		if(tokenProvider.validToken(token)) {
			Authentication authentication = tokenProvider.getAuthentication(token);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		
		filterChain.doFilter(request, response);
	}
	
	private String getAccessToken(String authorizationHeader) {
		if(authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
			return authorizationHeader.substring(TOKEN_PREFIX.length());
		}
		return null;
	}
}




