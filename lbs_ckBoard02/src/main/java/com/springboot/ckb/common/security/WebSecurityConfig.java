package com.springboot.ckb.common.security;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.springboot.ckb.common.security.jwt.TokenProvider;
import com.springboot.ckb.common.security.oauth.CustomLogoutHandler;
import com.springboot.ckb.common.security.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.springboot.ckb.common.security.oauth.OAuth2SuccessHandler;
import com.springboot.ckb.common.security.repository.OauthMemRepository;
import com.springboot.ckb.common.security.repository.RefreshTokenRepository;
import com.springboot.ckb.common.security.service.CustomOAuth2UserService;
import com.springboot.ckb.common.security.service.CustomUserDetails;
import com.springboot.ckb.common.security.service.SercurityMemberService;
import com.springboot.ckb.common.security.service.UserDetailService;
import com.springboot.ckb.member.dto.SessionUser;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SercurityMemberService sercurityMemberService;
    private final OauthMemRepository oauthMemberRepository;
    private final UserDetailService customUserDetailsService;
    private final CustomLogoutHandler customLogoutHandler;
    
    @Bean
    public WebSecurityCustomizer configure() {
        return web -> web.ignoring()
                .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, @Lazy OAuth2SuccessHandler oAuth2SuccessHandler) throws Exception {
        http
            // 🔹 1. 불필요한 기본 설정 비활성화
            .csrf(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable)

            // 🔹 2. 세션 정책: 필요 시만 생성 (JWT 병행 구조)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )

            // 🔹 3. JWT 인증 필터 등록
            .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)

            // 🔹 4. 접근 권한 설정
            .authorizeHttpRequests(auth -> auth
//                 API 요청만 보호
                .requestMatchers("/api/**").authenticated()
//                 ✅ 관리자 전용 페이지
                .requestMatchers("/member/listMembers.do", "/member/removeMember.do").hasRole("ADMIN")
//                 로그인, 회원가입, OAuth2, 정적리소스는 항상 허용
                .requestMatchers("/member/registerMember", "/member/memberForm", "/member/loginForm").permitAll()
                .requestMatchers("/admin/registration/memberForm").permitAll()
                .requestMatchers("/admin/registration/sendCode", "/admin/registration/verifyCode").permitAll()
                .requestMatchers("/admin/registration/adminMemberForm", "/admin/registration/createAdmin").permitAll()
                
//                .requestMatchers("/admin/registration/adminMemberForm",
//                        "/admin/registration/createAdmin").hasRole("ADMIN")
                // 나머지 요청도 허용 (게시판 메인 접근 가능)
             // 🔥 관리자만 허용되는 URL 패턴
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().permitAll()
//                .anyRequest().authenticated()
            )

            // 🔹 5. 일반 로그인 설정 (버튼 클릭 시만 표시)
            .formLogin(form -> form
                .loginPage("/member/loginForm")
                .loginProcessingUrl("/formLogin")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler((request, response, authentication) -> {
                	HttpSession session = request.getSession();

            	   // ✅ CustomUserDetails에서 Member 꺼내서 세션 저장
            	    CustomUserDetails customUser = (CustomUserDetails) authentication.getPrincipal();
            	    SessionUser loginUser = new SessionUser(customUser.getMember());
            	    session.setAttribute("loginUser", loginUser);
            	    session.setAttribute("isLogOn", true);

            	    String redirect = (String) session.getAttribute("action");
            	    session.removeAttribute("action");

            	    response.sendRedirect(redirect != null ? redirect : "/main");
                })
                .failureUrl("/member/loginForm?loginFailed=true")
                .permitAll()
            )

            // 🔹 6. OAuth2 로그인
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login") // 로그인 버튼 클릭 시 /login으로 이동
                .authorizationEndpoint(a -> a.baseUri("/oauth2/authorization"))
                .userInfoEndpoint(u -> u.userService(customOAuth2UserService))
                .successHandler(oAuth2SuccessHandler)
                .failureUrl("/member/loginForm?loginFailed=true")
            )

            // 🔹 7. 로그아웃 설정
            .logout(logout -> logout
                .logoutUrl("/logout")
//                .logoutSuccessUrl("/main")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessHandler(customLogoutHandler)
            )
            // 개발환경: 서버 재시작 후 기존 세션 자동 로그인 방지
            .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )
        

            // 🔹 8. 인증 예외 처리 (API 전용)
            .exceptionHandling(exception -> exception
            	    // 1️⃣ /api/** 인증 안 된 경우 401
//            	    .defaultAuthenticationEntryPointFor(
//            	        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
//            	        new AntPathRequestMatcher("/api/**")
//            	    )
            		 .authenticationEntryPoint((request, response, authException) -> {
            	            // 인증 안 된 경우 로그인 페이지로
            			 response.setStatus(HttpServletResponse.SC_FORBIDDEN);
         	            request.setAttribute("errorMessage", "접근 권한이 없습니다.");
         	            request.getRequestDispatcher("/error/403").forward(request, response);
            	        })
            	    // 2️⃣ 권한 부족 시 403 처리
            	    .accessDeniedHandler(new AccessDeniedHandler() {
            	        @Override
            	        public void handle(HttpServletRequest request,
            	                           HttpServletResponse response,
            	                           AccessDeniedException accessDeniedException) throws IOException, ServletException {
            	            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            	            request.setAttribute("errorMessage", "접근 권한이 없습니다.");
            	            request.getRequestDispatcher("/error/403").forward(request, response);
            	        }
            	    })
            	)
            	.csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler(@Lazy TokenProvider tokenProvider) {
        return new OAuth2SuccessHandler(
            tokenProvider,
            refreshTokenRepository,
            oAuth2AuthorizationRequestBasedOnCookieRepository(),
            sercurityMemberService,
            oauthMemberRepository
        );
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider);
    }

    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository
    oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    // ✅ 일반 로그인용 AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authManagerBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());

        return authManagerBuilder.build();
    }

    @Bean
    @Primary
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
//    	 return NoOpPasswordEncoder.getInstance();
    }
}
