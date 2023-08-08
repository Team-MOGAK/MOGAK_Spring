package com.mogak.spring.service;

import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.repository.JogakRepository;
import com.mogak.spring.web.dto.MogakRequestDto;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.assertj.core.api.SoftAssertions.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import java.time.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(
        properties = "schedules.cron.reward.publish=0/2 * * * * ?"
)
class JogakServiceImplTest {

    @Autowired
    private JogakRepository jogakRepository;

    @Autowired
    private MogakService mogakService;

    @Autowired
    private JogakService jogakService;

    @Test
    @DisplayName("스케줄러를 통해서 조각생성 확인")
    void 스케줄러_조각생성_테스트() {
        //given
        MogakRequestDto.CreateDto req1 =
                MogakRequestDto.CreateDto.builder()
                        .userId(1L)
                        .title("스프링 해야딩")
                        .category("직무공부")
                        .days(List.of("SATURDAY", "SUNDAY"))
                        .startAt(LocalDate.now())
                        .endAt(LocalDate.now().plusDays(7))
                        .build();

        MogakRequestDto.CreateDto req2 =
                MogakRequestDto.CreateDto.builder()
                        .userId(1L)
                        .title("스프링가링가링")
                        .category("직무공부")
                        .days(List.of("MONDAY", "TUESDAY"))
                        .startAt(LocalDate.now())
                        .endAt(LocalDate.now().plusDays(7))
                        .build();
        mogakService.create(req1);
        mogakService.create(req2);

        //when
//        jogakService.createJogakByScheduler();
        List<Jogak> jogaks = jogakRepository.findAll();

        //then
        for (Jogak jogak: jogaks) {
            System.out.println("조각 = " + jogak.toString());
        }
        SoftAssertions softly = new SoftAssertions();
        Awaitility.await()
                .atMost(3, TimeUnit.SECONDS)
                .untilAsserted (() ->
                    softly.assertThat(jogaks).isNotEmpty()
                );
    }

    @Test
    @DisplayName("시작하지_않은_조각_불러오기")
    void 시작하지_않은_조각_불러오기_테스트() {
        MogakRequestDto.CreateDto req =
                MogakRequestDto.CreateDto.builder()
                        .userId(1L)
                        .title("슈우웅")
                        .category("직무공부")
                        .days(List.of("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"))
                        .startAt(LocalDate.now())
                        .endAt(LocalDate.now().plusDays(7))
                        .build();
        mogakService.create(req);
        jogakService.createJogak(1L);

        List<Jogak> jogaks = jogakRepository.findJogakByState(null);
        assertThat(jogaks.size()).isEqualTo(3);
    }

}