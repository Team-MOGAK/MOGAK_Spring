package com.mogak.spring.web.dto.userdto;

import com.mogak.spring.jwt.JwtTokens;

public class UserResponseDto {
    public record CreateDto(Long userId, String nickname, JwtTokens tokens) {
    }

    public record LoginDto(String token) {
    }

    public record UserDto(String nickname, String job) {
    }

    public record GetUserDto(String nickname, String job, String imgUrl) {
    }
}
