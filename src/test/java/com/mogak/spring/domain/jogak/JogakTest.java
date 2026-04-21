package com.mogak.spring.domain.jogak;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class JogakTest {

    @Test
    @DisplayName("하루 시작 시각과 다음날 새벽 4시 마감 시각을 계산한다")
    void 날짜_데드라인_테스트() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime deadLine = LocalDateTime.now().toLocalDate().atStartOfDay().plusDays(1).plusHours(4);

        System.out.println("startOfDay = " + startOfDay);
        System.out.println("deadLine = " + deadLine);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(startOfDay.toLocalTime()).isEqualTo(LocalTime.of(0,0,0));
        softly.assertThat(deadLine.toLocalTime()).isEqualTo(LocalTime.of(4,0,0));
    }
}
