package com.mogak.spring.converter;

import com.mogak.spring.domain.common.Validation;
import com.mogak.spring.domain.user.Address;
import com.mogak.spring.domain.user.Job;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.web.dto.userdto.UserRequestDto;
import com.mogak.spring.web.dto.userdto.UserResponseDto;

public class UserConverter {

    public static User toUser(UserRequestDto.CreateUserDto response, Job job, Address address, String profileImgUrl, String profileImgName) {
        return User.builder()
                .nickname(response.getNickname())
                .job(job)
                .address(address)
                .email(response.getEmail())
                .validation(Validation.ACTIVE.toString())
                .profileImgUrl(profileImgUrl)
                .profileImgName(profileImgName)
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
                .build();
    }

}
