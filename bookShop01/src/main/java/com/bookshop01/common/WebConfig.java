package com.bookshop01.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.bookshop01.common.interceptor.ViewNameInterceptor;

@Configuration
//@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ViewNameInterceptor())
                .addPathPatterns("/**") // 모든 요청에 적용 (필요 시 조정)
        		.excludePathPatterns("/member/overlapped.do");     // 중복체크 요청은 
    }
}
