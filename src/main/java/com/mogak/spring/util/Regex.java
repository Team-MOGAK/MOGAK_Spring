package com.mogak.spring.util;

public enum Regex {
    //유저 닉네임 정규표현식
    USER_NICKNAME_REGEX("^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{3,10}$", "NICKNAME"),

    //이메일 정규표현식
    EMAIL_REGEX("[0-9a-zA-Z]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$", "EMAIL");

    private final String regex;
    private final String code;
    Regex(String regex, String code) {
        this.regex = regex;
        this.code = code;
    }

    public Boolean matchRegex(String input, String code) {
        for (Regex regex: values()) {
            System.out.println("code.equals(regex.code = " + code.equals(regex.code));

            if (code.equals(regex.code)) {
                System.out.println("input.matches(this.regex) = " + input.matches(this.regex));
                return input.matches(this.regex);
            }
        }
        return false;
    }
}
