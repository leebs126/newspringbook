package com.springboot.ckb.common.security.service;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.springboot.ckb.common.security.domain.OauthMember;
import com.springboot.ckb.member.domain.Member;

public class CustomUserDetails implements UserDetails, OAuth2User {

    private Member member;                  // 일반 로그인 회원
    private OauthMember oauthUser;          // OAuth2 로그인 회원
    private Map<String, Object> attributes; // OAuth2 attributes

    // ✅ 일반 로그인용 생성자
    public CustomUserDetails(Member member) {
        this.member = member;
        this.attributes = Collections.emptyMap();
    }

    // ✅ OAuth2 로그인용 생성자
    public CustomUserDetails(OauthMember oauthUser, Map<String, Object> attributes) {
        this.oauthUser = oauthUser;
        this.attributes = attributes != null ? attributes : Collections.emptyMap();
    }

    // ✅ getter
    public Member getMember() {
        return member;
    }

    public OauthMember getOauthUser() {
        return oauthUser;
    }

    // ✅ OAuth2User 구현 메서드
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        // member가 null이면 oauthUser의 이메일 또는 sub 값 사용
        if (member != null && member.getMemId() != null) {
            return member.getMemId();
        } else if (oauthUser != null && oauthUser.getEmail() != null) {
            return oauthUser.getEmail();
        } else {
            Object sub = attributes.get("sub");
            return sub != null ? sub.toString() : null;
        }
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (member != null && member.getRole() != null) {
            return Collections.singleton(new SimpleGrantedAuthority(member.getRole()));
        }

        // OAuth2 회원 (role 필드가 없을 수도 있음)
        if (oauthUser != null) {
            return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
        }

        // 기본값: 일반 사용자
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
    }

//    // ✅ UserDetails 구현 메서드
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return Collections.singleton(() -> "ROLE_USER");
//    }

    @Override
    public String getPassword() {
        if (member != null) return member.getPwd();
        // OAuth 로그인은 비밀번호가 없을 수 있음
        return null;
    }

    @Override
    public String getUsername() {
        if (member != null) return member.getMemId();
        if (oauthUser != null) return oauthUser.getEmail();
        Object email = attributes.get("email");
        return email != null ? email.toString() : null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
