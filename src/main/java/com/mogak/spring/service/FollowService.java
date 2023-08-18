package com.mogak.spring.service;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static com.mogak.spring.web.dto.FollowRequestDto.*;
import static com.mogak.spring.web.dto.UserResponseDto.*;

public interface FollowService {

    void follow(String nickname, HttpServletRequest req);

    void unfollow(String nickname, HttpServletRequest req);

    CountDto getFollowCount(String nickname);

    List<UserDto> getMotoList(String nickname);

    List<UserDto> getMentorList(String nickname);
}
