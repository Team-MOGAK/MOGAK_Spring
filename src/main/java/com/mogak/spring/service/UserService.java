package com.mogak.spring.service;

import com.mogak.spring.domain.user.User;
import com.mogak.spring.web.dto.UserRequestDto;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;

public interface UserService {

    User create(UserRequestDto.CreateUserDto request);
    Boolean findUserByNickname(String nickname);
    Boolean verifyNickname(String request);
    HttpHeaders getHeader(User user);
    User findUserByEmail(String email);

    void updateNickname(UserRequestDto.UpdateNicknameDto nicknameDto, HttpServletRequest req);

    void updateJob(UserRequestDto.UpdateJobDto jobDto, HttpServletRequest req);
}
