package com.mogak.spring.repository;

import com.mogak.spring.domain.mogak.MogakCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MogakCategoryRepository extends JpaRepository<MogakCategory, Long> {
    Optional<MogakCategory> findMogakCategoryByName(String name);
}
