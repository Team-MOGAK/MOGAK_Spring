package com.mogak.spring.service;

import com.mogak.spring.domain.user.User;
import com.mogak.spring.web.dto.UserRequestDto;
import com.mogak.spring.web.dto.UserResponseDto;

import java.util.Optional;

public interface UserService {

    User create(UserRequestDto.CreateUserDto request);
    Boolean findUserByNickname(String nickname);
    Boolean verifyNickname(String request);
    UserResponseDto.LoginDto getLoginDto(User user);
    User findUserByEmail(String email);
}
