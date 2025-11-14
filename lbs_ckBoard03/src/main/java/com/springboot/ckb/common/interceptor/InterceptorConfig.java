package com.springboot.ckb.common.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private ViewNameInterceptor viewNameInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 인터셉터를 등록하고 적용할 URL 패턴 지정
        registry.addInterceptor(viewNameInterceptor)
                .addPathPatterns("/**")              // 모든 요청에 적용
                .excludePathPatterns("/css/**", "/js/**", "/images/**"); // 정적 리소스 제외
    }
}
