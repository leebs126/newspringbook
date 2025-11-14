package com.springboot.ckb.common.security.repository;


import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.springboot.ckb.member.domain.Member;



@Mapper
public interface MemRepository {

    // 이메일로 회원 조회
//	Optional<Member> findByEmail(@Param("email") String email);
	Member findByEmail(@Param("email") String email);
	
	Member findByMemId(@Param("memId") String memId);
    // 회원 정보 저장
    void insertMember(Member member);

    // 회원 정보 수정
    void updateMember(Member member);

    // 회원 삭제
    void deleteMember(@Param("id") Long id);

    // id로 회원 조회
    Optional<Member> findById(@Param("id") String id);
    
 // ✅ 회원 전체 목록 조회 메서드 추가
    List<Member> selectAllMemberList();
}
