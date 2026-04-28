package com.mogak.spring.converter;

import com.mogak.spring.domain.user.Address;
import com.mogak.spring.domain.user.Job;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.web.dto.userdto.UserRequestDto;
import com.mogak.spring.web.dto.userdto.UserResponseDto;

public class UserConverter {

    public static User toUser(UserRequestDto.CreateUserDto response, Job job, Address address, String profileImgUrl, String profileImgName) {
        return User.builder()
                .nickname(response.nickname())
                .job(job)
                .address(address)
//                .email(response.getEmail())
                .profileImgUrl(profileImgUrl)
                .profileImgName(profileImgName)
                .build();
    }

    public static UserResponseDto.CreateDto toCreateDto(User user) {
        return new UserResponseDto.CreateDto(user.getId(), user.getNickname(), null);
    }

    public static UserResponseDto.LoginDto toLoginDto(String jwtToken) {
        return new UserResponseDto.LoginDto(jwtToken);
    }

    public static UserResponseDto.UserDto toUserDto(User user) {
        return new UserResponseDto.UserDto(user.getNickname(), user.getJob().getName());
    }

}
