package com.mogak.spring.repository;

import com.mogak.spring.domain.mogak.MogakCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MogakCategoryRepository extends JpaRepository<MogakCategory, Long> {
    Optional<MogakCategory> findMogakCategoryByName(String name);
}
