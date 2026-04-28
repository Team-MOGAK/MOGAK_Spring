package com.mogak.spring.repository;

import com.mogak.spring.domain.modarat.Modarat;
import com.mogak.spring.repository.query.ModaratDetailProjection;
import com.mogak.spring.repository.query.MogakInModaratProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ModaratRepository extends JpaRepository<Modarat, Long> {
    @Query("SELECT NEW com.mogak.spring.repository.query.ModaratDetailProjection(m.id, m.title, m.color) " +
            "FROM Modarat m " +
            "WHERE m.id = :modaratId AND m.deletedAt is null")
    ModaratDetailProjection findOneDetailModarat(@Param("modaratId") Long modaratId);

    @Query("SELECT NEW com.mogak.spring.repository.query.MogakInModaratProjection" +
            "(m.title, m.bigCategory, m.smallCategory, m.color) " +
            "FROM Mogak m join m.modarat mt " +
            "WHERE mt.id = :modaratId AND m.deletedAt is null AND mt.deletedAt is null")
    Optional<List<MogakInModaratProjection>> findMogaksByModaratId(@Param("modaratId") Long modaratId);

    @Query("select m from Modarat m where m.id = :modaratId and m.deletedAt is null and m.user.deletedAt is null")
    Optional<Modarat> findActiveById(@Param("modaratId") Long modaratId);

    @Query("select m from Modarat m where m.user.id = :userId and m.deletedAt is null and m.user.deletedAt is null")
    List<Modarat> findModaratsByUserId(@Param("userId") Long userId);

}
