package com.bookshop01.mypage.vo;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component("myPageVO")
public class MyPageVO {
	private String member_id;
	private String beginDate;
	private String endDate;

}
