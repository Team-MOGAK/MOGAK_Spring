package com.mogak.spring.service;

import com.mogak.spring.util.Regex;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

class UserServiceImplTest {

    @Test
    void 닉네임_유효성_테스트() {
        //given
        String nick1 = "hyun1234";
        String nick2 = "hyun!";
        String nick3 = "1234!";
        String rightNick = "hyun1234!";

        //when
        Boolean res1 = Regex.USER_NICKNAME_REGEX.matchRegex(nick1);
        Boolean res2 = Regex.USER_NICKNAME_REGEX.matchRegex(nick2);
        Boolean res3 = Regex.USER_NICKNAME_REGEX.matchRegex(nick3);
        Boolean rightRes = Regex.USER_NICKNAME_REGEX.matchRegex(rightNick);

        //then
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(res1).isFalse();
        softly.assertThat(res2).isFalse();
        softly.assertThat(res3).isFalse();
        softly.assertThat(rightRes).isTrue();
    }

}