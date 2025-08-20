package com.bookshop01.admin.member.dao;

import java.util.ArrayList;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.dao.DataAccessException;

import com.bookshop01.member.vo.MemberVO;

@Mapper
public interface AdminMemberDAO {
	public ArrayList<MemberVO> selectListMember(Map condMap) throws DataAccessException;
	public MemberVO selectMemberDetail(String member_id) throws DataAccessException;
	public void updateMemberInfo(Map memberMap) throws DataAccessException;
}
