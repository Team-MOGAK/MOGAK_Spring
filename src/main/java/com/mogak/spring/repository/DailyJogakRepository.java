package com.mogak.spring.repository;

import com.mogak.spring.domain.jogak.DailyJogak;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyJogakRepository extends JpaRepository<DailyJogak, Long> {

}
