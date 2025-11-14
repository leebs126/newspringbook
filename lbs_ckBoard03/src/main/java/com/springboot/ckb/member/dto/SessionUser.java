package com.springboot.ckb.member.dto;

import com.springboot.ckb.common.security.domain.OauthMember;
import com.springboot.ckb.member.domain.Member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionUser {
    private final String memId;
    private final String nickname;
    private final String email;
    private final String provider; // "LOCAL" or "GOOGLE"

    // 일반 로그인용
    public SessionUser(Member member) {
        this.memId = member.getMemId();
        this.email = member.getEmail();
        this.nickname = member.getNickname(); // ✅ 일반 회원 닉네임도 추가
        this.provider = "LOCAL";
    }

    // OAuth2 로그인용
    public SessionUser(OauthMember oauthUser) {
        this.memId = oauthUser.getMemId();  // 또는 필요 시 email 사용
        this.email = oauthUser.getEmail();
        this.nickname = oauthUser.getName(); // ✅ 닉네임 가져오기
        this.provider = "GOOGLE";
    }

}
