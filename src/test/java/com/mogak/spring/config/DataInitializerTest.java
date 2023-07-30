package com.mogak.spring.config;

import com.mogak.spring.domain.mogak.MogakCategory;
import com.mogak.spring.repository.MogakCategoryRepository;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.NoSuchElementException;
import java.util.Optional;

@DataJpaTest
class DataInitializerTest {

    @Autowired
    private MogakCategoryRepository mogakCategoryRepository;


    @Test
    @DisplayName("카테고리 객체가 잘 생성되는지")
    void 모각카테고리_생성_test() {
        //given, when
        MogakCategory certification = DataInitializer.categoryConstructor("자격증");
        MogakCategory jobStudy = DataInitializer.categoryConstructor("직무공부");

        //then
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(certification.getName()).isEqualTo("자격증");
        softly.assertThat(jobStudy.getName()).isEqualTo("직무공부");
    }

}