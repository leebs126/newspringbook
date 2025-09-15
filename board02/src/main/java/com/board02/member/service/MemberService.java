package com.board02.member.service;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.board02.member.vo.MemberVO;

public interface MemberService {
	 public List<MemberVO> listMembers() throws DataAccessException;
	 public int addMember(MemberVO memberVO) throws DataAccessException;
	 public int removeMember(String memId) throws DataAccessException;
	 public MemberVO login(MemberVO memberVO) throws Exception;
}
