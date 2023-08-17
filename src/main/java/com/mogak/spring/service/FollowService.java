package com.mogak.spring.service;

import javax.servlet.http.HttpServletRequest;

public interface FollowService {

    void follow(String nickname, HttpServletRequest req);
}
