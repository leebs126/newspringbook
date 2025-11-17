package com.springboot.ckb.common.security.admin;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "admin.registration")
public class AdminRegistrationProperties {

    /**
     * 관리자 등록 코드 리스트
     */
    private List<String> codes;

    /**
     * 관리자 회원 가입 시 forbidden keyword 체크 무시 여부
     */
    private boolean ignoreForbidden;

    // ====================== getter / setter ======================

    public List<String> getCodes() {
        return codes;
    }

    public void setCodes(List<String> codes) {
        this.codes = codes;
    }

    public boolean isIgnoreForbidden() {
        return ignoreForbidden;
    }

    public void setIgnoreForbidden(boolean ignoreForbidden) {
        this.ignoreForbidden = ignoreForbidden;
    }
}
