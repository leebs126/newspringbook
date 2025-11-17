package com.springboot.ckb.common.security.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.springboot.ckb.common.security.admin.AdminProperties;
import com.springboot.ckb.common.security.admin.AdminRegistrationProperties;
import com.springboot.ckb.common.security.admin.SignupProperties;
import com.springboot.ckb.common.security.repository.SecurityMemberRepository;
import com.springboot.ckb.member.domain.Member;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SercurityMemberService {
	
	private final SecurityMemberRepository securityMemberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AdminProperties adminProperties;  // ✅ 관리자 목록 주입
    private final SignupProperties signupProperties; // ✅ 추가
    private final AdminRegistrationProperties adminRegProps;
    
    public void save(Member member) {
    	String memIdLower = member.getMemId().toLowerCase();
    	
    	 // 1️⃣ application.yml에서 금지 단어 읽어와 체크
        List<String> forbiddenKeywords = signupProperties.getForbiddenKeywords();
        if (forbiddenKeywords != null) {
            for (String keyword : forbiddenKeywords) {
                if (memIdLower.contains(keyword.toLowerCase())) {
                    throw new IllegalArgumentException(
                        "보안상 '" + keyword + "'가 포함된 아이디는 사용할 수 없습니다.");
                }
            }
        }
        // 비밀번호 암호화
        member.setPwd(bCryptPasswordEncoder.encode(member.getPwd()));

        // ✅ 관리자 목록에 포함된 아이디면 ROLE_ADMIN
        List<String> adminIds = adminProperties.getIds();
        if (adminIds != null && adminIds.stream()
                .anyMatch(adminId -> member.getMemId().equalsIgnoreCase(adminId))) {
            member.setRole("ROLE_ADMIN");
        } else {
            member.setRole("ROLE_USER");
        }

        securityMemberRepository.insertMember(member);
    }
    
    
    public Member findById(String memId) {
    	return securityMemberRepository.findById(memId)
    			.orElseThrow(()-> new IllegalArgumentException("Unexpected user"));
    }
    
    public Optional<Member> findByEmail(String email) {
    	return Optional.ofNullable(securityMemberRepository.findByEmail(email)); 
    }
    
    
    //관리자용 save() 메서드
    public void saveAdmin(Member member) {

        String memIdLower = member.getMemId().toLowerCase();

        // 금지 단어 체크 (관리자라도 금지)
        if(adminRegProps.isIgnoreForbidden()) {
	        List<String> forbiddenKeywords = signupProperties.getForbiddenKeywords();
	        if (forbiddenKeywords != null) {
	            for (String keyword : forbiddenKeywords) {
	                if (memIdLower.contains(keyword.toLowerCase())) {
	                    throw new IllegalArgumentException(
	                        "보안상 '" + keyword + "'가 포함된 아이디는 사용할 수 없습니다.");
	                }
	            }
	        }
        }

        // 비밀번호 암호화 필수!
        member.setPwd(bCryptPasswordEncoder.encode(member.getPwd()));

        // 관리자 권한 직접 부여
        member.setRole("ROLE_ADMIN");
        securityMemberRepository.insertMember(member);
    }

}
