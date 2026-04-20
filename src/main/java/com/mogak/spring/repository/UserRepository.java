package com.mogak.spring.repository;

import com.mogak.spring.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 닉네임으로 유저 조회
    Optional<User> findOneByNickname(String nickname);
    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    @Query("select u from User u where u.id = :id and u.deletedAt is null")
    Optional<User> findActiveById(@Param("id") Long id);

    @Query("select u from User u where u.email = :email and u.deletedAt is null")
    Optional<User> findActiveByEmail(@Param("email") String email);

    @Query("select u from User u where u.nickname = :nickname and u.deletedAt is null")
    Optional<User> findActiveByNickname(@Param("nickname") String nickname);

    boolean existsByEmailAndDeletedAtIsNull(String email);
}
