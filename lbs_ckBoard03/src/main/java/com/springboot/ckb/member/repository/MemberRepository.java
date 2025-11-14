package com.springboot.ckb.member.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.dao.DataAccessException;

import com.springboot.ckb.member.domain.Member;



@Mapper
public interface MemberRepository {
	 public List selectAllMemberList() throws DataAccessException;
	 public int insertMember(Member memberVO) throws DataAccessException ;
	 public int deleteMember(String id) throws DataAccessException;
	 public Member loginById(Member memberVO) throws DataAccessException;
}
