package com.springboot.ckb.common.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String issuer;
    private String secretKey;
//	private String issuer = "leebs126@gmail.com";
//	private String secretKey = "dev-springboot-secret-key-for-jwt-token-1234567890";
}
