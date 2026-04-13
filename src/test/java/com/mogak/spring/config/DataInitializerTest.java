package com.mogak.spring.config;

import com.mogak.spring.domain.mogak.MogakCategory;
import com.mogak.spring.repository.MogakCategoryRepository;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.NoSuchElementException;
import java.util.Optional;


@DataJpaTest
@ActiveProfiles("test")
class DataInitializerTest {

    @Autowired
    private MogakCategoryRepository mogakCategoryRepository;

    @Test
    @DisplayName("카테고리 객체가 잘 생성되는지")
    void 모각카테고리_생성_test() {
        //given, when
        MogakCategory certification = category("자격증");
        MogakCategory jobStudy = category("직무공부");

        //then
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(certification.getName()).isEqualTo("자격증");
        softly.assertThat(jobStudy.getName()).isEqualTo("직무공부");
    }

    @Test
    @DisplayName("카테고리 데이터 insert")
    void 카테고리데이터_insert() {
        //given
        MogakCategory certification = category("자격증");
        MogakCategory jobStudy = category("직무공부");

        //when
        mogakCategoryRepository.save(certification);
        mogakCategoryRepository.save(jobStudy);

        Optional<MogakCategory> certificationInJPA = mogakCategoryRepository.findMogakCategoryByName("자격증");
        Optional<MogakCategory> jobStudyInJPA = mogakCategoryRepository.findMogakCategoryByName("직무공부");
        Optional<MogakCategory> noDataInJPA = mogakCategoryRepository.findMogakCategoryByName("노우");
        System.out.println(noDataInJPA);

        //then
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(certificationInJPA.get().getName()).isEqualTo("자격증");
        softly.assertThat(jobStudyInJPA.get().getName()).isEqualTo("직무공부");
        softly.assertThatThrownBy(noDataInJPA::get).isInstanceOf(NoSuchElementException.class);
        softly.assertAll();
    }

    private static MogakCategory category(String name) {
        return MogakCategory.builder()
                .name(name)
                .build();
    }
}
