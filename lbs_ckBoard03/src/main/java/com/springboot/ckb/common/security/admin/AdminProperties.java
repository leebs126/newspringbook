package com.springboot.ckb.common.security.admin;


import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "admin")
public class AdminProperties {
    private List<String> ids;
}
