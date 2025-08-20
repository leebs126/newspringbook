package com.bookshop01.member.service;

import java.util.Map;

import com.bookshop01.member.vo.MemberVO;

public interface MemberService {
	public MemberVO login(Map<String, String>   loginMap) throws Exception;
	public void addMember(MemberVO memberVO) throws Exception;
	public String overlapped(String id) throws Exception;
}
