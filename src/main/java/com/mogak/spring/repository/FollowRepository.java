package com.mogak.spring.repository;

import com.mogak.spring.domain.user.Follow;
import com.mogak.spring.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    @Query("SELECT f " +
            "FROM Follow f " +
            "WHERE f.fromUser = :from and f.toUser = :to")
    Optional<Follow> findByFromAndTo(User from, User to);
}
