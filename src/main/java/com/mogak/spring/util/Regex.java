package com.mogak.spring.util;

import com.mogak.spring.web.dto.UserRequestDto;

public enum Regex {
    //유저 닉네임 정규표현식
    USER_NICKNAME_REGEX("^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{3,10}$"),

    //이메일 정규표현식
    EMAIL_REGEX("[0-9a-zA-Z]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$");

    private String regex;
    Regex(String regex) {
        this.regex = regex;
    }

    public Boolean matchRegex(String input) {
        return input.matches(this.regex);
    }
}
