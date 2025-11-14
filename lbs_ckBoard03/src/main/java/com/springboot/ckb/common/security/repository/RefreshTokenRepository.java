package com.springboot.ckb.common.security.repository;


import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.springboot.ckb.common.security.domain.RefreshToken;

@Mapper
public interface RefreshTokenRepository {

    // userId로 RefreshToken 조회
    RefreshToken findByUserId(@Param("userId") String userId);

    // refreshToken 값으로 조회
    Optional<RefreshToken> findByRefreshToken(@Param("refreshToken") String refreshToken);

    // 새로운 RefreshToken 저장
    void insertRefreshToken(RefreshToken token);

    // RefreshToken 갱신
    void updateRefreshToken(@Param("userId") String userId,
                            @Param("refreshToken") String refreshToken);

    // 특정 사용자 토큰 삭제
    void deleteByUserId(@Param("userId") String userId);
}
