package com.bookshop01.common;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalAttributes {

    @ModelAttribute("savingPoints")
    public Integer expectedPoint() {
        return 2500;   //적립금
    }
}
