package com.example.reactmapping.domain.post.service;

import com.example.reactmapping.domain.post.entity.Post;
import com.example.reactmapping.domain.post.entity.PostComment;
import com.example.reactmapping.domain.post.repository.PostRepository;
import com.example.reactmapping.domain.post.repository.PostCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;


    public Page<Post> findAll(Pageable pageable) {
        return postRepository.findAll(pageable);
    }
    @Transactional
    public void savePost(Post post){
        postRepository.save(post);
    }
    public Optional<Post> findPostById(Long postId) {
        return postRepository.findById(postId);
    }
    public Page<Post> findAllPost(PageRequest pageRequest) {
        return postRepository.findAll(pageRequest);
    }

}
