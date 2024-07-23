package com.example.reactmapping.domain.post.service;

import com.example.reactmapping.domain.post.domain.Post;
import com.example.reactmapping.domain.post.domain.PostComment;
import com.example.reactmapping.domain.post.repository.PostRepository;
import com.example.reactmapping.domain.post.repository.PostCommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;

    public void savePost(Post post){
        postRepository.save(post);
    }
    public Optional<Post> findPostById(Long postId) {
        return postRepository.findById(postId);
    }
    public Page<Post> findAllPost(PageRequest pageRequest) {
        return postRepository.findAll(pageRequest);
    }
    public List<PostComment> findAllComments(List<Long> postIds) {
        return postCommentRepository.findAllComments(postIds);
    }
}
