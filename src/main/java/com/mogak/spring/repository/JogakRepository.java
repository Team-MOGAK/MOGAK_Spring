package com.mogak.spring.repository;

import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JogakRepository extends JpaRepository<Jogak, Long> {
    @Query(value = "SELECT j " +
            "FROM Jogak j join fetch j.mogak m join fetch m.user " +
            "where m.user = :user " +
            "AND j.createdAt >= CURRENT_DATE AND j.createdAt < CURRENT_DATE + 1")
    List<Jogak> findDailyJogak(@Param(value = "user") User user);

    @Query(value = "SELECT j FROM Jogak j WHERE :state Is NULL or j.state = :state")
    List<Jogak> findJogakByState(@Param(value = "state") String state);

//    @Query(value = "SELECT j FROM Jogak j WHERE j.state = :state " +
//            "AND j.startTime < CURRENT_DATE AND j.createdAt >= CURRENT_DATE - 1")
//    List<Jogak> findJogakIsOngoingYesterday(@Param(value = "state") String state);

    List<Jogak> findAllByMogak(Mogak mogak);
}
