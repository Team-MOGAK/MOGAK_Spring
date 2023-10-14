package com.mogak.spring.repository;

import com.mogak.spring.domain.mogak.Mogak;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MogakRepository extends JpaRepository<Mogak, Long> {
    List<Mogak> findAllByUserIdOrderByCreatedAtDesc(Long userId, PageRequest pageRequest);
//    @Query(value = "select m from Mogak m " +
//            "join fetch m.bigCategory join fetch m.mogakPeriods mp join fetch mp.period " +
//            "where m.state = :state and mp.period.id = :today - 1 ")
//    List<Mogak> findAllOngoingToday(@Param("state") String state, @Param("today") int today);
    List<Mogak> findAllByEndAt(LocalDate now);
}
