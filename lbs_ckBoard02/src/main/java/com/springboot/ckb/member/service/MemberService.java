package com.springboot.ckb.member.service;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.springboot.ckb.member.domain.Member;


public interface MemberService {
	 public List<Member> listMembers() throws DataAccessException;
	 public int modifyMember(Member member) throws DataAccessException;
	 public int removeAccount(String memId) throws DataAccessException;
	 public Member findMemberById(String memId) throws Exception;
}
