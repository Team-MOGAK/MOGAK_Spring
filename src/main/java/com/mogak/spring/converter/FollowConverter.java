package com.mogak.spring.converter;

import com.mogak.spring.domain.user.Follow;
import com.mogak.spring.domain.user.User;

public class FollowConverter {

    public static Follow toFollow(User from, User to) {
        return Follow.builder()
                .fromUser(from)
                .toUser(to)
                .build();
    }
}
