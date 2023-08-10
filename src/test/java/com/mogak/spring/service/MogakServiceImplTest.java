package com.mogak.spring.service;

import com.mogak.spring.domain.common.State;
import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.jogak.JogakState;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.repository.JogakRepository;
import com.mogak.spring.repository.MogakRepository;
import com.mogak.spring.web.dto.MogakRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class MogakServiceImplTest {

    @Autowired
    private MogakService mogakService;

    @Autowired
    private MogakRepository mogakRepository;

    @Autowired
    private JogakRepository jogakRepository;

    @Test
    @DisplayName("성취율 계산 테스트")
    void 성취율_계산_테스트() {
        //given
        MogakRequestDto.CreateDto req =
                MogakRequestDto.CreateDto.builder()
                        .userId(1L)
                        .title("슈우웅")
                        .category("직무공부")
                        .days(List.of("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"))
                        .startAt(LocalDate.now())
                        .endAt(LocalDate.now().plusDays(7))
                        .build();
        Mogak mogak = mogakService.create(req);
        Jogak jogak1 = Jogak.builder()
                .mogak(mogak)
                .state(JogakState.ONGOING.name())
                .startTime(LocalDateTime.now())
                .build();

        Jogak jogak2 = Jogak.builder()
                .mogak(mogak)
                .state(JogakState.SUCCESS.name())
                .startTime(LocalDateTime.now())
                .build();

        Jogak jogak3 = Jogak.builder()
                .mogak(mogak)
                .state(JogakState.SUCCESS.name())
                .startTime(LocalDateTime.now().minusDays(1))
                .build();

        Jogak jogak4 = Jogak.builder()
                .mogak(mogak)
                .state(null)
                .startTime(LocalDateTime.now().minusDays(1))
                .build();

        List<Jogak> jogaks = new ArrayList<>();
        jogaks.add(jogak1);
        jogaks.add(jogak2);
        jogaks.add(jogak3);
        jogaks.add(jogak4);

        //when
        int success = 0;
        for (Jogak jogak: jogaks) {
            if (jogak.getState() == null) continue;
            if (jogak.getState().equals(JogakState.SUCCESS.name())) {
                success += 1;
            }
        }
        double rate = (double) success / jogaks.size() * 100;

        //then
        assertThat(rate).isEqualTo(50.0);
    }

    @Test
    @DisplayName("모각 결과 테스트")
    public void 모각_결과_테스트() {
        //given
        MogakRequestDto.CreateDto req =
                MogakRequestDto.CreateDto.builder()
                        .userId(1L)
                        .title("슈우웅")
                        .category("직무공부")
                        .days(List.of("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"))
                        .startAt(LocalDate.now())
                        .endAt(LocalDate.now().plusDays(3))
                        .build();

        Mogak mogak = mogakService.create(req);

        Jogak jogak1 = Jogak.builder()
                .mogak(mogak)
                .state(JogakState.SUCCESS.name())
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(1))
                .build();

        Jogak jogak2 = Jogak.builder()
                .mogak(mogak)
                .state(JogakState.SUCCESS.name())
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(3))
                .build();

        jogakRepository.save(jogak1);
        jogakRepository.save(jogak2);

        mogakService.judgeMogakByDay(LocalDate.now().plusDays(3));
        Optional<Mogak> mogak1 = mogakRepository.findById(5L);
        assertThat(mogak1.get().getState()).isEqualTo(State.COMPLETE.name());
    }

}