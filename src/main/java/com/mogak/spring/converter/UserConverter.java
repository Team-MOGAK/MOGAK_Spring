package com.mogak.spring.converter;

import com.mogak.spring.domain.user.Address;
import com.mogak.spring.domain.user.Job;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.web.dto.UserRequestDto;
import com.mogak.spring.web.dto.UserResponseDto;

public class UserConverter {

    public static User toUser(UserRequestDto.CreateUserDto response, Job job, Address address) {
        return User.builder()
                .nickname(response.getNickname())
                .job(job)
                .address(address)
                .profileImg(response.getProfileImg())
                .email(response.getEmail())
                .build();
    }
}
