package com.springboot.ckb.common.security.controller;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {
	/**
     * 클라이언트에서 /oauth2/authorize/google?redirect=/new-article 로 요청하면
     * 세션에 redirectURI 를 저장한 뒤 실제 Spring Security OAuth2 시작 엔드포인트로 리다이렉트합니다.
     */
    @GetMapping("/oauth2/authorize/{provider}")
    public void oauth2Authorize( @PathVariable("provider") String provider,
                                @RequestParam(value = "redirectURI", required = false) String redirectURI,
                                @RequestParam(value = "groupNO", required = false) String groupNO,
                                @RequestParam(value = "parentNO", required = false) String parentNO,
                                HttpServletRequest request,
                                HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession();
        if (redirectURI != null && !redirectURI.isEmpty()) {
        	session.setAttribute("redirectURI", redirectURI);
        	session.setAttribute("groupNO", groupNO);
        	session.setAttribute("parentNO", parentNO);
        }

        // 실제 Spring Security가 사용하는 엔드포인트로 보내기
        response.sendRedirect("/oauth2/authorization/" + provider);
    }
}
