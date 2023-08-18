package com.mogak.spring.repository;

import com.mogak.spring.domain.user.Follow;
import com.mogak.spring.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    @Query( "SELECT f " +
            "FROM Follow f " +
            "WHERE f.fromUser = :from and f.toUser = :to")
    Optional<Follow> findByFromAndTo(@Param("from")User from, @Param("to")User to);

    @Query("SELECT COUNT(f) FROM Follow f WHERE f.toUser = :user")
    int findMotoCntByUser(@Param("user")User user);

    @Query("SELECT COUNT(f) FROM Follow f WHERE f.fromUser = :user")
    int findMentorCntByUser(@Param("user")User user);
}
