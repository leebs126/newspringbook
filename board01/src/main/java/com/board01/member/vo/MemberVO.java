package com.board01.member.vo;

import java.sql.Date;
import org.springframework.stereotype.Component;
import lombok.Data;

@Data
@Component("memberVO")
public class MemberVO {
	private String memId;
	private String pwd;
	private String name;
	private String email;
	private Date joinDate;
	
	public MemberVO() {
		
	}

	public MemberVO(String memId, String pwd, String name, String email) {
		this.memId = memId;
		this.pwd = pwd;
		this.name = name;
		this.email = email;
	}
}
