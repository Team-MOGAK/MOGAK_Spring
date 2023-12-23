package com.mogak.spring.service;

import java.util.List;

import static com.mogak.spring.web.dto.userdto.FollowRequestDto.CountDto;
import static com.mogak.spring.web.dto.userdto.UserResponseDto.UserDto;

public interface FollowService {

    void follow(String nickname);

    void unfollow(String nickname);

    CountDto getFollowCount(String nickname);

    List<UserDto> getMotoList(String nickname);

    List<UserDto> getMentorList(String nickname);
}
