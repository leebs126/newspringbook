package com.springboot.ckb.common.security.repository;

import java.util.Optional;

import com.springboot.ckb.common.security.domain.OauthMember;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface OauthMemRepository {

    // 이메일로 Oauth 사용자 조회
    Optional<OauthMember> findByEmail(@Param("email") String email);

    // provider + providerId로 사용자 조회
    Optional<OauthMember> findByProviderAndProviderId(@Param("provider") String provider,
                                                      @Param("providerId") String providerId);

    // Oauth 사용자 등록
    void insertOauthMember(OauthMember oauthUser);

    // Oauth 사용자 정보 수정
    void updateOauthMember(OauthMember oauthUser);

    // 사용자 삭제
    void deleteById(@Param("id") Long id);
}
