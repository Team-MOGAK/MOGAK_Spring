package com.mogak.spring.repository;

import com.mogak.spring.domain.user.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Long> {
    Optional<Job> findJobByName(String job);

}
