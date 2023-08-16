package com.mogak.spring.repository;

import com.mogak.spring.domain.common.State;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.service.MogakService;
import com.mogak.spring.web.dto.MogakRequestDto;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@SpringBootTest
class MogakRepositoryTest {

    @Autowired
    private MogakService mogakService;
    @Autowired
    private MogakRepository mogakRepository;

    @Test
    @DisplayName("진행중이며 오늘 해야하는 모각 불러오기")
    void 해야하는_모각_불러오기_테스트() {
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        int dayNum = dayOfWeek.getValue();

        MogakRequestDto.CreateDto req1 =
                MogakRequestDto.CreateDto.builder()
                        .userId(1L)
                        .title("스프링 해야딩")
                        .category("직무공부")
                        .days(List.of("MONDAY", "TUESDAY", "SATURDAY", "SUNDAY"))
                        .startAt(LocalDate.now())
                        .endAt(LocalDate.now().plusDays(7))
                        .build();
        mogakService.create(req1, req);

        List<Mogak> mogaks =  mogakRepository.findAllOngoingToday(State.ONGOING.name(), dayNum);
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(mogaks.size()).isEqualTo(1);


    }


}