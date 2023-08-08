package com.mogak.spring.repository;

import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JogakRepository extends JpaRepository<Jogak, Long> {
    @Query(value = "SELECT j FROM Jogak j WHERE j.mogak.user = :user " +
            "AND j.createdAt >= CURRENT_DATE AND j.createdAt < CURRENT_DATE + 1")
    List<Jogak> findDailyJogak(@Param(value = "user") User user);
}
