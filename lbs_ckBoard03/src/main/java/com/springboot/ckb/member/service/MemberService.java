package com.springboot.ckb.member.service;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.springboot.ckb.member.domain.Member;


public interface MemberService {
	 public List<Member> listMembers() throws DataAccessException;
	 public int addMember(Member memberVO) throws DataAccessException;
	 public int removeMember(String memId) throws DataAccessException;
	 public Member login(Member memberVO) throws Exception;
}
