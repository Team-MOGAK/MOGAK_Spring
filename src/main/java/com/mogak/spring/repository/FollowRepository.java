package com.mogak.spring.repository;

import com.mogak.spring.domain.user.Follow;
import com.mogak.spring.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
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

    @Query("SELECT f.fromUser " +
            "FROM Follow f " +
            "JOIN f.toUser JOIN f.fromUser fu JOIN FETCH fu.job " +
            "WHERE f.toUser = :user")
    List<User> findMotosByUser(@Param("user") User user);

    @Query("SELECT f.toUser " +
            "FROM Follow f " +
            "JOIN f.fromUser JOIN f.toUser tu JOIN FETCH tu.job " +
            "WHERE f.fromUser = :user")
    List<User> findMentorsByUser(@Param("user") User user);
}
