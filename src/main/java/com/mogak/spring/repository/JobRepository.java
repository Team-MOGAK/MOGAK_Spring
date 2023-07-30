package com.mogak.spring.repository;

import com.mogak.spring.domain.user.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {

}
