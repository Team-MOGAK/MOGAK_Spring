package com.mogak.spring.repository;

import com.mogak.spring.domain.mogak.Period;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PeriodRepository extends JpaRepository<Period, Integer> {

    //요일 불러오는 메소드
    Optional<Period> findOneByDays(String day);
}
