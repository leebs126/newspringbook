package com.springboot.ckb.common.security.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ✅ MyBatis 기반 OauthMember VO 클래스
 * JPA의 @Entity, @Id, @Column, @GeneratedValue, @SequenceGenerator 제거
 * → 단순히 DB 컬럼 매핑용 필드만 유지
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OauthMember {

    private Long id;               // PK
    private String memId;
    private String email;          // 이메일 (unique)
    private String name;           // 이름
    private String provider;       // 제공자 (예: google, naver 등)
    private String providerId;     // 제공자 식별 ID
    private LocalDateTime createdAt; // 가입 시간
    

    @Builder
    public OauthMember(String email, String name, String provider, String providerId) {
        this.email = email;
        this.name = name;
        this.provider = provider;
        this.providerId = providerId;
        this.createdAt = LocalDateTime.now(); // 기본값
    }
}
