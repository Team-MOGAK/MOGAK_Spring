package com.mogak.spring.repository;

import com.mogak.spring.domain.mogak.MogakPeriod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MogakPeriodRepository extends JpaRepository<MogakPeriod, Long> {

    List<MogakPeriod> findAllByMogak_Id(Long id);
    void deleteAllByMogakId(Long mogakId);

}
