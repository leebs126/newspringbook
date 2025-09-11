package com.board01.member.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.board01.member.repository.MemberRepository;
import com.board01.member.vo.MemberVO;

@Service
@Primary
@Transactional(propagation = Propagation.REQUIRED)
public class MemberServiceImpl implements MemberService {
	@Autowired
	private MemberRepository memberRepository;

	@Override
	public List<MemberVO> listMembers() throws DataAccessException {
		List<MemberVO> membersList = null;
		membersList = memberRepository.selectAllMemberList();
		return membersList;
	}

	@Override
	public int addMember(MemberVO member) throws DataAccessException {
		return memberRepository.insertMember(member);
	}

	@Override
	public int removeMember(String memId) throws DataAccessException {
		return memberRepository.deleteMember(memId);
	}
	
	@Override
	public MemberVO login(MemberVO memberVO) throws Exception{
		return memberRepository.loginById(memberVO);
	}

}
