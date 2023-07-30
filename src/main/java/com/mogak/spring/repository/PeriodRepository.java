package com.mogak.spring.repository;

import com.mogak.spring.domain.mogak.Period;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PeriodRepository extends JpaRepository<Period, Integer> {
}
