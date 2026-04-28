package com.mogak.spring.web.dto.userdto;

import com.mogak.spring.domain.user.User;

public record UserSummaryResponse(String nickname, String job) {
    public static UserSummaryResponse from(User user) {
        return new UserSummaryResponse(user.getNickname(), user.getJob().getName());
    }
}
