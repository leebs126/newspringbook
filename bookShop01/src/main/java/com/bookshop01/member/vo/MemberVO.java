package com.bookshop01.member.vo;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component("memberVO")
public class MemberVO {
    private String memberId;
    private String memberPw;
    private String memberName;
    private String memberGender;
    private String memberBirthY;
    private String memberBirthM;
    private String memberBirthD;
    private String memberBirthGn;
    private String tel1;
    private String tel2;
    private String tel3;
    private String hp1;
    private String hp2;
    private String hp3;
    private String smsstsYn;
    private String email1;
    private String email2;
    private String emailstsYn;
    private String zipcode;
    private String roadAddress;
    private String jibunAddress;
    private String namujiAddress;
    private String joinDate;
    private String delYn;
}

