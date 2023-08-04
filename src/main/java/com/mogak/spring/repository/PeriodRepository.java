package com.mogak.spring.repository;

import com.mogak.spring.domain.mogak.Period;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PeriodRepository extends JpaRepository<Period, Integer> {

    //요일 불러오는 메소드
    Period findOneByDays(String day);
}
