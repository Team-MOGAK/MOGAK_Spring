package com.mogak.spring.repository;

import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.post.PostImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostImgRepository extends JpaRepository<PostImg, Long> {

    List<PostImg> findAllByPost(Post post);
}
