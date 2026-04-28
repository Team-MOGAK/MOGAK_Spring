package com.mogak.spring.service.result.post;

public record NetworkUserResult(
        String nickname,
        String job
) {
    public static NetworkUserResult of(String nickname, String job) {
        return new NetworkUserResult(nickname, job);
    }
}
