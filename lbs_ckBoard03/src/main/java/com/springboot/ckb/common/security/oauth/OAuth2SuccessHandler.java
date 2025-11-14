package com.springboot.ckb.common.security.oauth;

import java.io.IOException;
import java.time.Duration;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;

import com.springboot.ckb.common.security.domain.OauthMember;
import com.springboot.ckb.common.security.domain.RefreshToken;
import com.springboot.ckb.common.security.jwt.TokenProvider;
import com.springboot.ckb.common.security.repository.OauthMemRepository;
import com.springboot.ckb.common.security.repository.RefreshTokenRepository;
import com.springboot.ckb.common.security.service.CustomUserDetails;
import com.springboot.ckb.common.security.service.MemService;
import com.springboot.ckb.common.security.util.CookieUtil;
import com.springboot.ckb.member.dto.SessionUser;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
//@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
	public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
	public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1);
	public static final String REDIRECT_PATH = "/main.do";
	
	private final TokenProvider tokenProvider;
	private final RefreshTokenRepository refreshTokenRepository;
	private final OAuth2AuthorizationRequestBasedOnCookieRepository
				   authorizationRequestRepository;
	private final MemService memService;
	private final OauthMemRepository oauthMemberRepository;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
	                                    Authentication authentication) throws IOException, ServletException {

	    // 1️⃣ OAuth2User 정보 가져오기
	    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
	    String email = (String) oAuth2User.getAttributes().get("email");
	    String name = (String) oAuth2User.getAttributes().get("name");
	    String providerId = (String) oAuth2User.getAttributes().get("sub");

	    // 2️⃣ OAuth 전용 테이블(OauthMember)에 저장 또는 조회
	    Optional<OauthMember> optionalOauthMember = oauthMemberRepository.findByProviderAndProviderId("google", providerId);

	    OauthMember oauthUser = null;

	    if (optionalOauthMember.isPresent()) {
	        oauthUser = optionalOauthMember.get(); // 이미 존재하면 그대로 사용
	    } else {
	    	oauthUser = OauthMember.builder()
	                .email(email)
	                .name(name)
	                .provider("google")
	                .providerId(providerId)
	                .build();
	        oauthMemberRepository.insertOauthMember(oauthUser); // 존재하지 않을 때만 insert
	    }
	    
//	    OauthMember oauthMember = oauthMemberRepository.findByProviderAndProviderId("google", providerId)
//	            .orElseGet(() -> OauthMember.builder()
//	                    .email(email)
//	                    .name(name)
//	                    .provider("google")
//	                    .providerId(providerId)
//	                    .build()
//	            );
//	    oauthMemberRepository.insertOauthMember(oauthMember);

	    // 3️⃣ 기존 USERS 테이블과 연결할 필요가 있다면, User 엔티티 생성/조회
	    //    예: JWT 발급용 User 엔티티 (여기서는 이메일 기반으로 기존 USER를 조회하거나 임시 User 생성)
//	    User user = userService.findByEmail(email); // findOrCreateUserByEmail 메서드는 필요 시 구현
	    try {
//	        user = memService.findByEmail(email);
	    	oauthUser = oauthMemberRepository.findByEmail(email)
                    .orElseGet(() -> OauthMember.builder()
	                                           .email(email)
	                                           .name(name)
	                                           .build());
	    } catch (IllegalArgumentException e) {
	        // USERS 테이블에 없으면 임시 User 생성 (id는 null, JWT 발급만 목적)
	    	oauthUser = OauthMember.builder()
	                .email(email)
	                .name(name)
	                .build();
	    }
	    
	    // 4️⃣ 리프레시 토큰 생성 -> DB 저장 -> 쿠키에 저장
	    String refreshToken = tokenProvider.generateToken(oauthUser, REFRESH_TOKEN_DURATION);
	    saveRefreshToken(oauthUser.getEmail(), refreshToken);
	    addRefreshTokenCookie(request, response, refreshToken);

	    // 5️⃣ 엑세스 토큰 생성
	    String accessToken = tokenProvider.generateToken(oauthUser, ACCESS_TOKEN_DURATION);
	    String targetUrl = getTargetUrl(accessToken);

	    HttpSession session = request.getSession();

	 // ✅ 구글 로그인 또는 일반 로그인에 따른 세션 설정
	    Object principal = authentication.getPrincipal();
	    
	    SessionUser loginUser = null;
	    
	    if (principal instanceof CustomUserDetails customUser) {
            if (customUser.getOauthUser() != null) {
                loginUser = new SessionUser(customUser.getOauthUser());
            } else if (customUser.getMember() != null) {
                loginUser = new SessionUser(customUser.getMember());
            }
        } else if (principal instanceof OAuth2User oauth2User) {
            String _email = (String)oauth2User.getAttribute("email");
            oauthUser = oauthMemberRepository.findByEmail(_email).get();
            loginUser = new SessionUser(oauthUser);
        }

        if (loginUser != null) {
            session.setAttribute("loginUser", loginUser);
            session.setAttribute("isLogOn", true);
        }

//	    if (principal instanceof CustomUserDetails customUser) {
//	        // ✅ CustomUserDetails는 일반/구글 로그인 모두 감쌈
//
//	        if (customUser.getOauthUser() != null) {
//	            // ✅ 구글 로그인(OAuth2)
//	            session.setAttribute("member", customUser.getOauthUser());
//	            session.setAttribute("isLogOn", true);
//
//	        } else if (customUser.getMember() != null) {
//	            // ✅ 일반 로그인
//	            session.setAttribute("member", customUser.getMember());
//	            session.setAttribute("isLogOn", true);
//	        }
//
//	    } else if (principal instanceof OAuth2User oauth2User) {
//	        // ✅ 혹시 CustomUserDetails로 래핑되지 않은 순수 OAuth2 로그인 (예외 상황)
//	        String  _email = oauth2User.getAttribute("email");
//	        oauthUser = oauthMemberRepository.findByEmail(_email).get();
//	        session.setAttribute("member", oauthUser);
//	        session.setAttribute("isLogOn", true);
//	    }


	    String redirectURI = null;
	    if (session != null) {
	    	redirectURI = (String) session.getAttribute("redirectURI");
	        session.removeAttribute("redirectURI");
	    }
	    
//	    // ✅ 세션에서 redirectURI 꺼내기
//	    HttpSession session = request.getSession(false);
//	    
//	    //구글 로그인 시 세션에 로그온 상태 설정
//	    CustomOAuth2UserService customUser = (CustomOAuth2UserService) authentication.getPrincipal();
//	    session.setAttribute("member", customUser.get());
//	    session.setAttribute("isLogOn", true);
//	    String redirect = null;
//	    if (session != null) {
//	        redirect = (String) session.getAttribute("redirectURI");
//	        session.removeAttribute("redirectURI");
//	    }
//	    
	    if (redirectURI != null && !redirectURI.isEmpty()) {
	        targetUrl = redirectURI; // JWT를 붙이지 않고 redirect
	    } else {
	        targetUrl = getTargetUrl(accessToken);
	    }

	    clearAuthenticationAttributes(request, response);
	    getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}


	//생성된 리프레시 토큰을 전달받아 데이터베이스 저장
	private void saveRefreshToken(String  userId, String newRefreshToken) {
		RefreshToken existingToken = refreshTokenRepository.findByUserId(userId);

	    if (existingToken == null) {
	        // 기존 토큰이 없으면 새로 삽입
	        refreshTokenRepository.insertRefreshToken(new RefreshToken(userId, newRefreshToken));
	    } else if (!newRefreshToken.equals(existingToken.getRefreshToken())) {
	        // 토큰이 다를 때만 업데이트 (불필요한 DB UPDATE 방지)
	        refreshTokenRepository.updateRefreshToken(userId, newRefreshToken);
	    }
	}
	
	//생성된 리프레시 토큰을 쿠키에 저장
	private void addRefreshTokenCookie(HttpServletRequest request, HttpServletResponse response,
									String refreshToken) {
		int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();
		CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
		CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge);
	}
										
	//인증 관련 설정값, 쿠키 제거
	private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
		super.clearAuthenticationAttributes(request);
		authorizationRequestRepository.removeAuthorizationRequest(request, response);
	}
	
	//액세스 토큰을 패스에 추가
	private String getTargetUrl(String token) {
		return UriComponentsBuilder.fromUriString(REDIRECT_PATH)
				.queryParam("token", token)
				.build()
				.toUriString();
	}
	

}















