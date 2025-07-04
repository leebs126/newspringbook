package com.myboot03;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.myboot03.member")
@SpringBootApplication
public class Myboot03Application {

	public static void main(String[] args) {
		SpringApplication.run(Myboot03Application.class, args);
	}

}
