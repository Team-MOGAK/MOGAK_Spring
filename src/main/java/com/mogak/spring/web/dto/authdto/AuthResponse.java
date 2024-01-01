package com.mogak.spring.web.dto.authdto;

import lombok.*;


public class AuthResponse {

    @Getter
    @Builder
    public static class WithdrawDto{
        boolean isDeleted;
    }
}
