package com.springboot.ckb.common.security.admin;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "signup")
public class SignupProperties {

    private List<String> forbiddenKeywords;

    public List<String> getForbiddenKeywords() {
        return forbiddenKeywords;
    }

    public void setForbiddenKeywords(List<String> forbiddenKeywords) {
        this.forbiddenKeywords = forbiddenKeywords;
    }
}
