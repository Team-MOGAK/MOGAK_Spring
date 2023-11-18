package com.mogak.spring.web.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "로그인 API", description = "로그인 API 명세서")
@RestController
@RequiredArgsConstructor
@RequestMapping("/login")
public class AuthController {
}
