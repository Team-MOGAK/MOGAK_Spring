package com.mogak.spring.service;

import java.util.List;

import com.mogak.spring.service.result.FollowCountResult;
import com.mogak.spring.service.result.UserSummaryResult;

public interface FollowService {

    void follow(Long userId, String nickname);

    void unfollow(Long userId, String nickname);

    FollowCountResult getFollowCount(String nickname);

    List<UserSummaryResult> getMotoList(String nickname);

    List<UserSummaryResult> getMentorList(String nickname);
}
