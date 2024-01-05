package com.mogak.spring.service;

import com.mogak.spring.converter.UserConverter;
import com.mogak.spring.domain.user.Address;
import com.mogak.spring.domain.user.Job;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.util.Regex;
import com.mogak.spring.web.dto.userdto.UserRequestDto;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class UserServiceImplTest {

    @Test
    @DisplayName("닉네임_입력_유효성_테스트")
    void 닉네임_유효성_테스트() {
        //given
        String nick1 = "hyun1234";
        String nick2 = "hyun!";
        String nick3 = "1234!";
        String rightNick = "hyun1234!";

        //when
        Boolean res1 = Regex.USER_NICKNAME_REGEX.matchRegex(nick1, "NICKNAME");
        Boolean res2 = Regex.USER_NICKNAME_REGEX.matchRegex(nick2, "NICKNAME");
        Boolean res3 = Regex.USER_NICKNAME_REGEX.matchRegex(nick3, "NICKNAME");
        Boolean rightRes = Regex.USER_NICKNAME_REGEX.matchRegex(rightNick, "NICKNAME");

        //then
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(res1).isFalse();
        softly.assertThat(res2).isFalse();
        softly.assertThat(res3).isFalse();
        softly.assertThat(rightRes).isTrue();
        softly.assertAll();
    }

    @Test
    @DisplayName("DTO를_통한_유저_생성_테스트")
    void 유저_생성_테스트() {
        //when
        UserRequestDto.CreateUserDto response =
                UserRequestDto.CreateUserDto.builder()
                        .nickname("hyun1234!")
                        .job("개발/데이터")
                        .address("경기도")
//                        .email("hyun1234@naver.com")
                        .build();

        //given
        Job job = Job.builder()
                .name(response.getJob())
                .build();
        Address address = Address.builder()
                .name(response.getAddress())
                .build();
        String imgUrl = null;
        String imgName = null;

        //then
        assertThat(UserConverter.toUser(response, job, address, imgUrl, imgName))
                .isInstanceOf(User.class);
    }



}