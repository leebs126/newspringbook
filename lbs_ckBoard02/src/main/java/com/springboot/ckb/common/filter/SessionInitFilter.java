package com.springboot.ckb.common.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SessionInitFilter implements Filter {

    private  boolean sessionInitFlag = false;

//    public SessionInitFilter(SessionInitFlag sessionInitFlag) {
//        this.sessionInitFlag = sessionInitFlag;
//    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // 서버 실행 후 최초 요청에만 실행됨
        if (!sessionInitFlag) {

            HttpServletRequest httpReq = (HttpServletRequest) request;
            HttpSession session = httpReq.getSession(true); // 세션 생성

            session.setAttribute("isLogOn", false);

            // 이제 초기화 완료 → 다시는 실행 안 됨
            sessionInitFlag = true;
        }

        chain.doFilter(request, response);
    }
}
