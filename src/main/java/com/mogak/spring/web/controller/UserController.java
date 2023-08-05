package com.mogak.spring.web.controller;

import com.mogak.spring.converter.UserConverter;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.service.UserService;
import com.mogak.spring.web.dto.UserRequestDto;
import com.mogak.spring.web.dto.UserResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {
    private UserService userService;
    
    @PostMapping("/{nickname}/verify")
    public ResponseEntity<Object> verifyNickname(@PathVariable String nickname) {
        if (userService.findUserByNickname(nickname)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @PostMapping("/join")
    public ResponseEntity<UserResponseDto.toCreateDto> createUser(@RequestBody UserRequestDto.CreateUserDto request) {
        User user = userService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserConverter.toCreateDto(user));
    }
}
