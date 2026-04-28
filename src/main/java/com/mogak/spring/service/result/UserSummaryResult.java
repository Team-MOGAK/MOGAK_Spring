package com.mogak.spring.service.result;

import com.mogak.spring.domain.user.User;

public record UserSummaryResult(String nickname, String job) {
    public static UserSummaryResult from(User user) {
        return new UserSummaryResult(user.getNickname(), user.getJob().getName());
    }
}
