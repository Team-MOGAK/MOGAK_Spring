package com.mogak.spring.repository;

import com.mogak.spring.domain.jogak.DailyJogak;
import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DailyJogakRepository extends JpaRepository<DailyJogak, Long> {
    @Query("SELECT j FROM DailyJogak j WHERE j.targetDate BETWEEN :startDate AND :endDate AND j.deletedAt is null")
    List<DailyJogak> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT j FROM DailyJogak j WHERE j.targetDate = :targetDate AND j.jogak = :jogak AND j.deletedAt is null")
    Optional<DailyJogak> findActiveByJogakAndTargetDate(@Param("jogak") Jogak jogak,
                                                        @Param("targetDate") LocalDate targetDate);

    @Deprecated
    default Optional<DailyJogak> findByCreatedAtBetweenAndId(LocalDateTime startDateTime,
                                                             LocalDateTime endDateTime,
                                                             Jogak jogak) {
        return findActiveByJogakAndTargetDate(jogak, startDateTime.toLocalDate());
    }

    @Query("SELECT j from DailyJogak j " +
            "JOIN FETCH j.mogak jm JOIN FETCH jm.user u JOIN FETCH j.jogak jogak " +
            "WHERE jm.user = :user and j.targetDate = :targetDate " +
            "AND j.deletedAt is null AND jm.deletedAt is null AND u.deletedAt is null")
    List<DailyJogak> findDailyJogaks(@Param(value = "user") User user,
                                     @Param(value = "targetDate") LocalDate targetDate);

    @Deprecated
    default List<DailyJogak> findDailyJogaks(User user, LocalDateTime today, LocalDateTime tomorrow) {
        return findDailyJogaks(user, today.toLocalDate());
    }

    @Query("SELECT j from DailyJogak j " +
            "JOIN FETCH j.mogak jm JOIN FETCH jm.user u " +
            "WHERE jm.user = :user and j.targetDate >= :startDate and j.targetDate < :endDate " +
            "AND j.deletedAt is null AND jm.deletedAt is null AND u.deletedAt is null")
    List<DailyJogak> findDailyJogaksBetween(@Param(value = "user") User user,
                                            @Param(value = "startDate") LocalDate startDate,
                                            @Param(value = "endDate") LocalDate endDate);

    @Query("SELECT dj FROM DailyJogak dj " +
            "JOIN FETCH dj.jogak j " +
            "JOIN FETCH j.user " +
            "JOIN FETCH j.mogak " +
            "JOIN FETCH j.category " +
            "WHERE dj.id = :dailyJogakId AND dj.deletedAt is null " +
            "AND j.deletedAt is null AND j.user.deletedAt is null AND j.mogak.deletedAt is null")
    Optional<DailyJogak> findActiveByIdWithJogakGraph(@Param("dailyJogakId") Long dailyJogakId);

    @Deprecated
    default Optional<DailyJogak> findByIdWithJogakGraph(Long dailyJogakId) {
        return findActiveByIdWithJogakGraph(dailyJogakId);
    }

    @Query("SELECT dj FROM DailyJogak dj " +
            "JOIN FETCH dj.jogak j " +
            "JOIN FETCH j.user " +
            "JOIN FETCH j.mogak " +
            "JOIN FETCH j.category " +
            "WHERE j.id = :jogakId AND dj.targetDate = :targetDate AND dj.deletedAt is null " +
            "AND j.deletedAt is null AND j.user.deletedAt is null AND j.mogak.deletedAt is null")
    Optional<DailyJogak> findActiveByJogakIdAndTargetDateWithJogakGraph(@Param("jogakId") Long jogakId,
                                                                        @Param("targetDate") LocalDate targetDate);

    void deleteAllByJogak(Jogak jogak);

    @Query("select dj from DailyJogak dj where dj.jogak = :jogak and dj.deletedAt is null")
    List<DailyJogak> findActiveAllByJogak(@Param("jogak") Jogak jogak);
}
