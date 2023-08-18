//package com.mogak.spring.service;
//
//import com.mogak.spring.domain.jogak.Jogak;
//import com.mogak.spring.domain.jogak.JogakState;
//import com.mogak.spring.domain.mogak.Mogak;
//import com.mogak.spring.domain.mogak.MogakCategory;
//import com.mogak.spring.domain.user.User;
//import com.mogak.spring.repository.JogakRepository;
//import com.mogak.spring.repository.MogakRepository;
//import com.mogak.spring.web.dto.MogakRequestDto;
//import org.assertj.core.api.SoftAssertions;
//import org.awaitility.Awaitility;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.scheduling.annotation.EnableScheduling;
//
//import java.time.*;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//import static org.assertj.core.api.Assertions.*;
//
//@EnableScheduling
//@SpringBootTest(properties = {
//        "schedules.cron.reward.publish=0/3 * * * * *",
//        "spring.profiles.active=local"
//})
//class JogakServiceImplTest {
//
//    @Autowired
//    private JogakRepository jogakRepository;
//
//    @Autowired
//    private MogakRepository mogakRepository;
//
//    @Autowired
//    private JogakService jogakService;
//
////    @Test
////    @DisplayName("스케줄러를 통해서 조각생성 확인")
////    void 스케줄러_조각생성_테스트() {
////        //given
////        User user = User.builder()
////                .id(1L)
////                .nickname("hyun1234!@")
////                .validation("VALID")
////                .build();
////
////        MogakCategory mogakCategory = MogakCategory.builder()
////                .id(1)
////                .name("고옹부")
////                .build();
////
////        Mogak mogak1 = Mogak.builder()
////                .user(user)
////                .title("스프링 해야딩")
////                .category(mogakCategory)
////                .state("ONGOING")
////                .startAt(LocalDate.now())
////                .endAt(LocalDate.now().plusDays(7))
////                .validation("VALID")
////                .build();
////
////        Mogak mogak2 = Mogak.builder()
////                .user(user)
////                .title("스프링가링가링")
////                .category(mogakCategory)
////                .state("ONGOING")
////                .startAt(LocalDate.now())
////                .endAt(LocalDate.now().plusDays(30))
////                .validation("VALID")
////                .build();
////
////        mogakRepository.save(mogak1);
////        mogakRepository.save(mogak2);
////
////        //when
//////        jogakService.createJogakByScheduler();
//////        List<Jogak> jogaks = jogakRepository.findAll();
////
////        //then
////        SoftAssertions softly = new SoftAssertions();
////        Awaitility.await()
////                .atMost(3, TimeUnit.SECONDS)
////                .untilAsserted (() ->
////                    softly.assertThat(jogaks.size()).isEqualTo(4)
////                );
////        softly.assertAll();
////    }
//
//    @Test
//    @DisplayName("시작하지_않은_조각_불러오기")
//    void 시작하지_않은_조각_불러오기_테스트() {
//        User user = User.builder()
//                .id(1L)
//                .nickname("hyun1234!@")
//                .validation("VALID")
//                .build();
//
//        MogakCategory mogakCategory = MogakCategory.builder()
//                .id(1)
//                .name("고옹부")
//                .build();
//
//        Mogak mogak1 = Mogak.builder()
//                .user(user)
//                .title("스프링 해야딩")
//                .category(mogakCategory)
//                .state("ONGOING")
//                .startAt(LocalDate.now())
//                .endAt(LocalDate.now().plusDays(7))
//                .validation("VALID")
//                .build();
//
//        mogakRepository.save(mogak1);
//        jogakService.createJogak(1L);
//
//        List<Jogak> jogaks = jogakRepository.findJogakByState(null);
//        assertThat(jogaks.size()).isEqualTo(1);
//    }
//
//    @Test
//    @DisplayName("어제_시작한_조각_불러오기")
//    void 어제_시작한_조각_불러오기_테스트() {
//        User user = User.builder()
//                .id(1L)
//                .nickname("hyun1234!@")
//                .validation("VALID")
//                .build();
//
//        MogakCategory mogakCategory = MogakCategory.builder()
//                .id(1)
//                .name("고옹부")
//                .build();
//
//        Mogak mogak1 = Mogak.builder()
//                .user(user)
//                .title("스프링 해야딩")
//                .category(mogakCategory)
//                .state("ONGOING")
//                .startAt(LocalDate.now())
//                .endAt(LocalDate.now().plusDays(7))
//                .validation("VALID")
//                .build();
//
//        mogakRepository.save(mogak1);
//
//        Jogak jogak = Jogak.builder()
//                .mogak(mogak1)
//                .state(JogakState.ONGOING.name())
//                .startTime(LocalDateTime.now().minusDays(1))
//                .build();
//
//        jogakRepository.save(jogak);
//
//        int size = jogakRepository.findJogakIsOngoingYesterday(JogakState.ONGOING.name()).size();
//        assertThat(size).isEqualTo(1);
//    }
//
////    @Test
////    @DisplayName("조각_실패_업데이트")
////    void 조각_상태_실패처리_테스트() {
////        jogakService.failJogakAtMidnight();
////        SoftAssertions softly = new SoftAssertions();
////        List<Jogak> jogaks = jogakRepository.findAll();
////        jogaks.forEach(
////                jogak -> softly.assertThat(jogak.getState()).isEqualTo(JogakState.FAIL.name())
////        );
////        softly.assertAll();
////    }
//
//    @Test
//    @DisplayName("어졔_시작한_조각_실패_업데이트")
//    void 어제_시작한_조각_실패처리_테스트() {
//        //given
//        User user = User.builder()
//                .id(1L)
//                .nickname("hyun1234!@")
//                .validation("VALID")
//                .build();
//
//        MogakCategory mogakCategory = MogakCategory.builder()
//                .id(1)
//                .name("고옹부")
//                .build();
//
//        Mogak mogak1 = Mogak.builder()
//                .user(user)
//                .title("스프링 해야딩")
//                .category(mogakCategory)
//                .state("ONGOING")
//                .startAt(LocalDate.now())
//                .endAt(LocalDate.now().plusDays(7))
//                .validation("VALID")
//                .build();
//
//        Jogak jogak = Jogak.builder()
//                .mogak(mogak1)
//                .state(JogakState.ONGOING.name())
//                .startTime(LocalDateTime.now().minusDays(1))
//                .build();
//
//        mogakRepository.save(mogak1);
//        jogakRepository.save(jogak);
//
//        //when
//        jogakService.failJogakAtFour();
//
//        //then
//        SoftAssertions softly = new SoftAssertions();
//        List<Jogak> jogaks = jogakRepository.findJogakIsOngoingYesterday(JogakState.ONGOING.name());
//        jogaks.forEach(
//                jogak1 -> softly.assertThat(jogak1.getState()).isEqualTo(JogakState.FAIL.name())
//        );
//        softly.assertAll();
//    }
//
////    // 스케줄로 업데이트가 안된다....
////    @Test
////    @DisplayName("조각_실패_처리_BY_스케줄러")
////    void 조각_실패_처리_테스트() {
//////        List<Jogak> jogaks = jogakRepository.findAll();
////        //then
////        SoftAssertions softly = new SoftAssertions();
////        Awaitility.await()
////                .atMost(10, TimeUnit.SECONDS)
////                .until (() -> {
////                    List<Jogak> jogaks = jogakRepository.findAll();
////                    jogaks.forEach(jogak -> softly.assertThat(
////                            jogak.getState()).isEqualTo(JogakState.FAIL.name()));
////                    return true;
////                });
////        softly.assertAll();
////    }
//
//}