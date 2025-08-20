package com.bookshop01.admin.member.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bookshop01.admin.member.dao.AdminMemberDAO;
import com.bookshop01.member.vo.MemberVO;


@Service
@Primary
@Transactional(propagation=Propagation.REQUIRED)
public class AdminMemberServiceImpl implements AdminMemberService {
	@Autowired
	private AdminMemberDAO adminMemberDAO;
	
	@Override
	public ArrayList<MemberVO> listMember(Map condMap) throws Exception{
		return adminMemberDAO.selectListMember(condMap);
	}

	@Override
	public MemberVO memberDetail(String memberId) throws Exception{
		 return adminMemberDAO.selectMemberDetail(memberId);
	}
	
	@Override
	public void  modifyMemberInfo(Map memberMap) throws Exception{
		 adminMemberDAO.updateMemberInfo(memberMap);
	}
}
