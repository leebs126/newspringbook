package com.bookshop01.admin.member.dao;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.dao.DataAccessException;

import com.bookshop01.member.vo.MemberVO;

@Mapper
public interface AdminMemberDAO {
	public ArrayList<MemberVO> selectListMember(HashMap condMap) throws DataAccessException;
	public MemberVO selectMemberDetail(String member_id) throws DataAccessException;
	public void updateMemberInfo(HashMap memberMap) throws DataAccessException;
}
