package com.mogak.spring.repository;

import com.mogak.spring.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 닉네임으로 유저 조회
    Optional<User> findOneByNickname(String nickname);
}
