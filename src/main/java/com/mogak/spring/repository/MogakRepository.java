package com.mogak.spring.repository;

import com.mogak.spring.domain.mogak.Mogak;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MogakRepository extends JpaRepository<Mogak, Long> {
}
