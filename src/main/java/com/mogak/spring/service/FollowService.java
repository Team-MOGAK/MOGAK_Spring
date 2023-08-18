package com.mogak.spring.service;

import javax.servlet.http.HttpServletRequest;

import static com.mogak.spring.web.dto.FollowRequestDto.*;

public interface FollowService {

    void follow(String nickname, HttpServletRequest req);

    void unfollow(String nickname, HttpServletRequest req);

    CountDto getFollowCount(String nickname);
}
