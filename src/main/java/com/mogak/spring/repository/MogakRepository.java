package com.mogak.spring.repository;

import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface MogakRepository extends JpaRepository<Mogak, Long> {
    @Query
    List<Mogak> findAllByModaratId(Long modaratId);
//    @Query(value = "select m from Mogak m " +
//            "join fetch m.bigCategory join fetch m.mogakPeriods mp join fetch mp.period " +
//            "where m.state = :state and mp.period.id = :today - 1 ")
//    List<Mogak> findAllOngoingToday(@Param("state") String state, @Param("today") int today);
    List<Mogak> findAllByEndAt(LocalDate now);
    List<Mogak> findAllByUser(User user);

    void deleteByUserId(Long userId);
}
