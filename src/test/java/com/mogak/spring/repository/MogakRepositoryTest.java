//package com.mogak.spring.repository;
//
//import com.mogak.spring.domain.common.State;
//import com.mogak.spring.domain.mogak.Mogak;
//import com.mogak.spring.domain.mogak.MogakCategory;
//import com.mogak.spring.domain.user.User;
//import org.assertj.core.api.SoftAssertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.time.DayOfWeek;
//import java.time.LocalDate;
//import java.util.List;
//
//@SpringBootTest
//class MogakRepositoryTest {
//
//    @Autowired
//    private MogakRepository mogakRepository;
//
//    @Test
//    @DisplayName("진행중이며 오늘 해야하는 모각 불러오기")
//    void 해야하는_모각_불러오기_테스트() {
//        LocalDate today = LocalDate.now();
//        DayOfWeek dayOfWeek = today.getDayOfWeek();
//        int dayNum = dayOfWeek.getValue();
//
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
//        List<Mogak> mogaks = mogakRepository.findAllOngoingToday(State.ONGOING.name(), dayNum);
//        SoftAssertions softly = new SoftAssertions();
//        softly.assertThat(mogaks.size()).isEqualTo(1);
//
//
//    }
//
//
//}