package com.mogak.spring.repository;

import com.mogak.spring.domain.jogak.JogakPeriod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JogakPeriodRepository extends JpaRepository<JogakPeriod, Long> {

    List<JogakPeriod> findAllByJogak_Id(Long jogakId);
    void deleteAllByJogakId(Long jogakId);

}
