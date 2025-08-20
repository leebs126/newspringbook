package com.bookshop01.admin.member.service;

import java.util.ArrayList;
import java.util.Map;

import com.bookshop01.member.vo.MemberVO;

public interface AdminMemberService {
	public ArrayList<MemberVO> listMember(Map condMap) throws Exception;
	public MemberVO memberDetail(String memberId) throws Exception;
	public void  modifyMemberInfo(Map memberMap) throws Exception;
}
