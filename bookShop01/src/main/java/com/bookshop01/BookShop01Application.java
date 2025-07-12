package com.bookshop01;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.bookshop01.**.dao")  // 또는 dao 위치 전체 지정
//@MapperScan(basePackages = "com.bookshop01")  // 또는 dao 위치 전체 지정
public class BookShop01Application {

	public static void main(String[] args) {
		SpringApplication.run(BookShop01Application.class, args);
	}

}
