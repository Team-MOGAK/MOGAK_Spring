package com.mogak.spring.repository;

import com.mogak.spring.domain.modarat.Modarat;
import com.mogak.spring.repository.query.GetMogakInModaratDto;
import com.mogak.spring.repository.query.SingleDetailModaratDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ModaratRepository extends JpaRepository<Modarat, Long> {
    @Query("SELECT NEW com.mogak.spring.repository.query.SingleDetailModaratDto(m.id, m.title, m.color) " +
            "FROM Modarat m " +
            "WHERE m.id = :modaratId")
    SingleDetailModaratDto findOneDetailModarat(@Param("modaratId") Long modaratId);

    @Query("SELECT NEW com.mogak.spring.repository.query.GetMogakInModaratDto" +
            "(m.title, m.bigCategory, m.smallCategory, m.color) " +
            "FROM Mogak m join m.modarat mt " +
            "WHERE mt.id = :modaratId")
    Optional<List<GetMogakInModaratDto>> findMogakDtoListByModaratId(@Param("modaratId") Long modaratId);

    List<Modarat> findModaratsByUserId(Long userId);
}
