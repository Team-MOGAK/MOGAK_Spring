package com.mogak.spring.repository;

import com.mogak.spring.domain.mogak.Mogak;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MogakRepository extends JpaRepository<Mogak, Long> {
    List<Mogak> findAllByUserIdOrderByCreatedAtDesc(Long userId, PageRequest pageRequest);
}
