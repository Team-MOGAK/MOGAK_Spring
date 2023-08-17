package com.mogak.spring.repository;

import com.mogak.spring.domain.user.Follow;
import com.mogak.spring.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    @Query
    Boolean isExistAlreadyFollow(User from, User to);
}
