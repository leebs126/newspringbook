package com.springboot.ckb.member.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.ckb.member.repository.MemberRepository;
import com.springboot.ckb.member.domain.Member;


@Service
@Primary
@Transactional(propagation = Propagation.REQUIRED)
public class MemberServiceImpl implements MemberService {
	@Autowired
	private MemberRepository memberRepository;

	@Override
	public List<Member> listMembers() throws DataAccessException {
		List<Member> membersList = null;
		membersList = memberRepository.selectAllMemberList();
		return membersList;
	}

	@Override
	public int addMember(Member member) throws DataAccessException {
		return memberRepository.insertMember(member);
	}

	@Override
	public int removeMember(String memId) throws DataAccessException {
		return memberRepository.deleteMember(memId);
	}
	
	@Override
	public Member login(Member memberVO) throws Exception{
		return memberRepository.loginById(memberVO);
	}

}
