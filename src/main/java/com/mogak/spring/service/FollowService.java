package com.mogak.spring.service;

import com.mogak.spring.domain.user.User;
import com.mogak.spring.web.dto.UserResponseDto;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static com.mogak.spring.web.dto.FollowRequestDto.*;

public interface FollowService {

    void follow(String nickname, HttpServletRequest req);

    void unfollow(String nickname, HttpServletRequest req);

    CountDto getFollowCount(String nickname);

    List<UserResponseDto.UserDto> getMotoList(String nickname);

    List<UserResponseDto.UserDto> getMentorList(String nickname);
}
