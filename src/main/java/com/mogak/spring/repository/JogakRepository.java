package com.mogak.spring.repository;

import com.mogak.spring.domain.jogak.DailyJogak;
import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface JogakRepository extends JpaRepository<Jogak, Long> {

    @Query("SELECT j from Jogak j " +
            "JOIN FETCH j.mogak jm JOIN FETCH jm.user JOIN FETCH j.jogakPeriods jp JOIN FETCH jp.period " +
            "WHERE jm.user = :user and jp.period.id = :today " +
            "AND j.deletedAt is null AND jm.deletedAt is null AND jm.user.deletedAt is null")
    List<Jogak> findDailyRoutineJogaks(@Param(value = "user") User user, @Param(value = "today") int todayNum);

    @Query(value = "SELECT j FROM Jogak j WHERE :state Is NULL or j.state = :state")
    List<Jogak> findJogakByState(@Param(value = "state") String state);

//    @Query(value = "SELECT j FROM Jogak j WHERE j.state = :state " +
//            "AND j.startTime < CURRENT_DATE AND j.createdAt >= CURRENT_DATE - 1")
//    List<Jogak> findJogakIsOngoingYesterday(@Param(value = "state") String state);

    @Query("select j from Jogak j where j.mogak = :mogak and j.deletedAt is null and j.mogak.deletedAt is null")
    List<Jogak> findAllByMogak(@Param("mogak") Mogak mogak);

    @Query("select distinct j from Jogak j " +
            "join fetch j.mogak m " +
            "join fetch j.category c " +
            "left join fetch j.jogakPeriods jp " +
            "left join fetch jp.period " +
            "where j.mogak = :mogak and j.deletedAt is null and m.deletedAt is null")
    List<Jogak> findAllByMogakWithFetchGraph(@Param("mogak") Mogak mogak);

    @Query("select count(j) from Jogak j " +
            "where j.mogak = :mogak and j.deletedAt is null and j.mogak.deletedAt is null " +
            "and (j.endAt is null or j.endAt > :today)")
    long countActiveOpenByMogak(@Param("mogak") Mogak mogak, @Param("today") LocalDate today);

    @Query("select j from Jogak j " +
            "join fetch j.mogak m join fetch j.category c join j.user u " +
            "where u = :user and j.isRoutine = false " +
            "and j.deletedAt is null and m.deletedAt is null and u.deletedAt is null")
    List<Jogak> findActiveOneTimeJogaksByUser(@Param("user") User user);

    @Query("SELECT DISTINCT j FROM Jogak j JOIN FETCH j.jogakPeriods jp JOIN FETCH jp.period p " +
            "WHERE j.user.id = :userId AND j.isRoutine = true AND j.deletedAt is null AND j.user.deletedAt is null")
    List<Jogak> findAllRoutineJogaksByUser(@Param("userId") Long userId);

    @Query("select j from Jogak j where j.user.id = :userId and j.deletedAt is null and j.user.deletedAt is null")
    Optional<List<Jogak>> findAllByUserId(@Param("userId") Long userId);

    @Query("select j from Jogak j where j.id = :jogakId and j.deletedAt is null and j.user.deletedAt is null and j.mogak.deletedAt is null")
    Optional<Jogak> findActiveById(@Param("jogakId") Long jogakId);

    @Query("SELECT j FROM Jogak j " +
            "JOIN FETCH j.dailyJogaks dj " +
            "WHERE dj = :dailyJogak AND j.deletedAt is null AND dj.deletedAt is null")
    Optional<Jogak> findByDailyJogak(@Param("dailyJogak") DailyJogak dailyJogak);
}
