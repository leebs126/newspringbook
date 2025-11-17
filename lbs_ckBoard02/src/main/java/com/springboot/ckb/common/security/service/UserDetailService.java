package com.springboot.ckb.common.security.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.springboot.ckb.common.security.repository.SecurityMemberRepository;
import com.springboot.ckb.member.domain.Member;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserDetailService implements UserDetailsService {
	private final SecurityMemberRepository securityMemberRepository;

	@Override
	public UserDetails loadUserByUsername(String memId) throws UsernameNotFoundException {
		
				Member member = Optional.of(securityMemberRepository.findByMemId(memId))
	            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

		 return new CustomUserDetails(member);
		 
//	    return org.springframework.security.core.userdetails.User.builder()
//	            .username(member.getMemId())
//	            .password(member.getPwd()) // ✅ 여기 암호화된 값이어야 함
//	            .roles("USER")
//	            .build();
	}

//	@Override
//	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//		return userRepository.findByEmail(email)
//				.orElseThrow(()-> new IllegalArgumentException(email));
//	}
}
