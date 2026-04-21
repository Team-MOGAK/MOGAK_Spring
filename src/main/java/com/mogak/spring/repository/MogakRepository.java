package com.mogak.spring.repository;

import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MogakRepository extends JpaRepository<Mogak, Long> {
    @Query("select m from Mogak m where m.modarat.id = :modaratId and m.deletedAt is null and m.modarat.deletedAt is null")
    List<Mogak> findAllByModaratId(@Param("modaratId") Long modaratId);

    @Query("select m from Mogak m where m.id = :mogakId and m.deletedAt is null and m.user.deletedAt is null")
    Optional<Mogak> findActiveById(@Param("mogakId") Long mogakId);
//    @Query(value = "select m from Mogak m " +
//            "join fetch m.bigCategory join fetch m.mogakPeriods mp join fetch mp.period " +
//            "where m.state = :state and mp.period.id = :today - 1 ")
//    List<Mogak> findAllOngoingToday(@Param("state") String state, @Param("today") int today);
    @Query("select m from Mogak m where m.user = :user and m.deletedAt is null and m.user.deletedAt is null")
    List<Mogak> findAllByUser(@Param("user") User user);

    @Query("SELECT m FROM Mogak m JOIN FETCH m.jogaks j WHERE j = :jogak and m.deletedAt is null and j.deletedAt is null")
    Optional<Mogak> findByJogak(@Param("jogak") Jogak jogak);
}
