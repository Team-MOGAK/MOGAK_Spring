package com.mogak.spring.service;

import com.mogak.spring.domain.user.User;
import com.mogak.spring.web.dto.UserRequestDto;

public interface UserService {

    User create(UserRequestDto.CreateUserDto response);
    Boolean findUserByNickname(String nickname);
}
