package com.springboot.ckb.member.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.ckb.member.domain.Member;
import com.springboot.ckb.member.repository.MemberRepository;


@Service
@Primary
@Transactional(propagation = Propagation.REQUIRED)
public class MemberServiceImpl implements MemberService {
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private  PasswordEncoder passwordEncoder; // 주입

	@Override
	public List<Member> listMembers() throws DataAccessException {
		List<Member> membersList = null;
		membersList = memberRepository.selectAllMemberList();
		return membersList;
	}


	@Override
	public int removeAccount(String memId) throws DataAccessException {
		return memberRepository.deleteAccount(memId);
	}

	@Override
	public Member findMemberById(String memId) throws Exception {
		Member member =memberRepository.selectrMemberById(memId);
		return member;
	}

	@Override
	public int modifyMember(Member member) throws DataAccessException {
		 // 비밀번호가 비어있지 않으면 암호화
        if (member.getPwd() != null && !member.getPwd().isEmpty()) {
            String encodedPwd = passwordEncoder.encode(member.getPwd());
            member.setPwd(encodedPwd);
        }
		int result = memberRepository.updateMember(member);
		return result;
	}
}
