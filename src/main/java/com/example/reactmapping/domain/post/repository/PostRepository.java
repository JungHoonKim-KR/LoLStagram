package com.example.reactmapping.domain.post.repository;

import com.example.reactmapping.domain.post.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAll();

    @Override
    Page<Post> findAll(Pageable pageable);
}
