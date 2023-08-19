package com.mogak.spring.converter;

import com.mogak.spring.domain.common.Validation;
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
                .validation(Validation.ACTIVE.toString())
                .build();
    }

    public static UserResponseDto.ToCreateDto toCreateDto(User user) {
        return UserResponseDto.ToCreateDto.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .build();
    }

    public static UserResponseDto.LoginDto toLoginDto(String jwtToken) {
        return UserResponseDto.LoginDto.builder()
                .token(jwtToken)
                .build();
    }

    public static UserResponseDto.UserDto toUserDto(User user) {
        return UserResponseDto.UserDto.builder()
                .nickname(user.getNickname())
                .job(user.getJob().getName())
                .address(user.getAddress().getName())
                .build();
    }

}
