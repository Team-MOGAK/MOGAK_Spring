package com.mogak.spring.repository;

import com.mogak.spring.domain.modarat.Modarat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ModaratRepository extends JpaRepository<Modarat, Long> {
//    @Query("SELECT NEW com.mogak.spring.web.dto.ModaratDto.ModaratResponseDto.SingleDetailModaratDto(m.id, m.title, m.color, " +
//            "(SELECT NEW com.mogak.spring.web.dto.MogakResponseDto.GetMogakInModaratDto()) " +
//            "FROM Modarat m")
//    ModaratResponseDto.SingleDetailModaratDto findOneDetailModarat(Long modaratId);

    @Query("SELECT m FROM Modarat m WHERE m.id = :modaratId")
    Modarat findOneDetailModarat(Long modaratId);
}
