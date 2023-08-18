package com.mogak.spring.service;

import com.mogak.spring.domain.common.State;
import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.jogak.JogakState;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.mogak.MogakCategory;
import com.mogak.spring.domain.user.User;
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
        User user = User.builder()
                .id(1L)
                .nickname("hyun1234!@")
                .validation("VALID")
                .build();

        MogakCategory mogakCategory = MogakCategory.builder()
                .id(1)
                .name("고옹부")
                .build();

        Mogak mogak1 = Mogak.builder()
                .user(user)
                .title("스프링 해야딩")
                .category(mogakCategory)
                .state("ONGOING")
                .startAt(LocalDate.now())
                .endAt(LocalDate.now().plusDays(7))
                .validation("VALID")
                .build();

        Jogak jogak1 = Jogak.builder()
                .mogak(mogak1)
                .state(JogakState.ONGOING.name())
                .startTime(LocalDateTime.now())
                .build();

        Jogak jogak2 = Jogak.builder()
                .mogak(mogak1)
                .state(JogakState.SUCCESS.name())
                .startTime(LocalDateTime.now())
                .build();

        Jogak jogak3 = Jogak.builder()
                .mogak(mogak1)
                .state(JogakState.SUCCESS.name())
                .startTime(LocalDateTime.now().minusDays(1))
                .build();

        Jogak jogak4 = Jogak.builder()
                .mogak(mogak1)
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
    /*
    @Test
    @DisplayName("모각 결과 테스트")
    public void 모각_결과_테스트() {
        //given
        User user = User.builder()
                .id(1L)
                .nickname("hyun1234!@")
                .validation("VALID")
                .build();

        MogakCategory mogakCategory = MogakCategory.builder()
                .id(1)
                .name("고옹부")
                .build();

        Mogak mogak1 = Mogak.builder()
                .user(user)
                .title("스프링 해야딩")
                .category(mogakCategory)
                .state("ONGOING")
                .startAt(LocalDate.now())
                .endAt(LocalDate.now().plusDays(7))
                .validation("VALID")
                .build();

        Jogak jogak1 = Jogak.builder()
                .mogak(mogak1)
                .state(JogakState.SUCCESS.name())
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(1))
                .build();

        Jogak jogak2 = Jogak.builder()
                .mogak(mogak1)
                .state(JogakState.SUCCESS.name())
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(3))
                .build();

        jogakRepository.save(jogak1);
        jogakRepository.save(jogak2);

        mogakService.judgeMogakByDay(LocalDate.now().plusDays(3));
        Optional<Mogak> mogakOptional = mogakRepository.findById(5L);
        assertThat(mogakOptional.get().getState()).isEqualTo(State.COMPLETE.name());
    }

     */

}