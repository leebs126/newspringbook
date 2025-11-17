package com.springboot.ckb.common.security.repository;


import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.springboot.ckb.common.security.domain.RefreshToken;

@Mapper
public interface RefreshTokenMapper {

    // 토큰 저장 (INSERT)
    void insertRefreshToken(RefreshToken token);

    // userId로 조회
    Optional<RefreshToken> findByUserId(@Param("userId") Long userId);

    // 토큰 업데이트
    void updateRefreshToken(@Param("userId") Long userId,
                            @Param("refreshToken") String refreshToken);

    // 토큰 삭제
    void deleteByUserId(@Param("userId") Long userId);
}
