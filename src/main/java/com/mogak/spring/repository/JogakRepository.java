package com.mogak.spring.repository;

import com.mogak.spring.domain.jogak.Jogak;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JogakRepository extends JpaRepository<Jogak, Long> {
}
