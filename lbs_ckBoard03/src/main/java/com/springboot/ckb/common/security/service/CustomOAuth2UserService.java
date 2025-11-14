package com.springboot.ckb.common.security.service;

import java.util.Optional;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.springboot.ckb.common.security.domain.OauthMember;
import com.springboot.ckb.common.security.repository.OauthMemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final OauthMemRepository oauthMemRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 🔹 구글로부터 사용자 정보 받아오기
        OAuth2User oAuth2User = super.loadUser(userRequest);
        
        // 🔹 이메일 정보 추출
        String email = oAuth2User.getAttribute("email");

        // 🔹 DB에서 회원 조회 (없으면 예외)
        OauthMember oauthUser = oauthMemRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("회원정보를 찾을 수 없습니다."));

        // 🔹 CustomUserDetails로 래핑해서 반환

        // ✅ 이제 문제없이 반환 가능
        return new CustomUserDetails(oauthUser, oAuth2User.getAttributes());
    }
}
