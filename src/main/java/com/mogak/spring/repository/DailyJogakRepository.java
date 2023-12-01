package com.mogak.spring.repository;

import com.mogak.spring.domain.jogak.DailyJogak;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DailyJogakRepository extends JpaRepository<DailyJogak, Long> {
    @Query("SELECT j FROM DailyJogak j WHERE j.createdAt >= :startDate AND j.createdAt <= :endDate")
    List<DailyJogak> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
