package com.springboot.ckb.member.domain;

import java.sql.Date;
import java.util.Collection;
import java.util.List;

//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


import lombok.Builder;
import lombok.Data;

@Data
@Component("member")
public class Member{
	private String memId;
	private String pwd;
	private String name;
	private String email;
	private String nickname;
	private Date createdAt;
	private Date updatedAt;
	// ✅ 권한 필드 추가
    private String role;  // ROLE_USER 또는 ROLE_ADMIN
	
	public Member() {
		
	}

	@Builder
	public Member(String memId, String pwd, String name, String email, String nickname) {
		this.memId = memId;
		this.pwd = pwd;
		this.name = name;
		this.email = email;
		this.nickname = nickname;
	}
//	
//	@Override   //권한 반환
//	public Collection<? extends GrantedAuthority> getAuthorities() {
//		return  List.of(new SimpleGrantedAuthority("user"));
//	}
//	
//
//	@Override
//	public String getPassword() {
//		return pwd;
//	}
//
//	@Override
//	public String getUsername() {
//		return email;
//	}
//	
//	//계정 만료 여부 반환
//	@Override
//	public boolean isAccountNonExpired() {
//		// 만료 되었는지 확인하는 로직
//		return true;  // true --> 만료되지 않았음 
//	}
//	
//	//계정 잠금 여부 반환
//	@Override
//	public boolean isAccountNonLocked() {
//		// 계정 잠금되었는지 확인하는 로직
//		return true;  //true --> 잠금되지 않았음
//	}
//	
//	
//	//패스워드의 만료 여부 반환
//	@Override
//	public boolean isCredentialsNonExpired() {
//		// 패스워드가 만료되었는지 확인하는 로직
//		return true;  //true --> 만료되지 않았음
//	}
//	
//	//계정 사용 가능 여부 반환
//	@Override
//	public boolean isEnabled() {
//		// 계정 사용 가능한지 확인하는 로직
//		return true;  //true --> 사용 가능
//	}
//	
}
