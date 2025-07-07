package com.myboot03;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@MapperScan("com.myboot03.member")
@Slf4j
@SpringBootApplication
public class Myboot03Application {

	public static void main(String[] args) {
		SpringApplication.run(Myboot03Application.class, args);
	}

}
