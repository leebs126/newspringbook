package com.springboot.ckb.common.security.oauth;


import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class CustomLogoutHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication)
                                throws IOException, ServletException {
        HttpSession session = request.getSession(false);

        if (session != null) {
            // ✅ 세션의 isLogOn 속성 제거
            session.removeAttribute("isLogOn");
            session.removeAttribute("member");

            // ✅ 필요 시 전체 세션 무효화
            session.invalidate();
        }

        // SecurityContext 초기화
        SecurityContextHolder.clearContext();

        // JSESSIONID 쿠키 삭제
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        // 로그아웃 후 메인 페이지로 이동
        response.sendRedirect("/main.do");
    }
}
