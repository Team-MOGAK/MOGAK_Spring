package com.mogak.spring.service;

import com.mogak.spring.service.result.follow.FollowCountResult;
import com.mogak.spring.service.result.follow.FollowUserResult;

import java.util.List;

public interface FollowService {

    void follow(Long userId, String nickname);

    void unfollow(Long userId, String nickname);

    FollowCountResult getFollowCount(String nickname);

    List<FollowUserResult> getMotoList(String nickname);

    List<FollowUserResult> getMentorList(String nickname);
}
