package com.mogak.spring.repository;

import com.mogak.spring.domain.post.Post;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {


    //회고록 상세 조회를 위함

    //회고록 전체 조회 - 무한스크롤
    Slice<Post> findAllById(Long id);


}
