package com.example.reactmapping.domain.post.controller;

import com.example.reactmapping.domain.post.dto.PostCommentDto;
import com.example.reactmapping.domain.post.dto.PostDto;
import com.example.reactmapping.domain.post.service.PostService;
import com.example.reactmapping.domain.post.service.SavePostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/post")
@Slf4j
public class PostController {
    private final SavePostService savePostService;
    private final PostService postService;
    @PostMapping("/write/post")
    public void writePost(@RequestBody PostDto postDto){
        log.info("게시들 등록 시작");
        savePostService.savePost(postDto);
        log.info("게시글 등록 완료");
    }
    @GetMapping("/postList")
    public Page<PostDto> getPostList(@PageableDefault(size = 3,direction = Sort.Direction.DESC, sort = "id")  Pageable pageable) {
        log.info("게시글 불러오기");
        return postService.findAll(pageable).map(PostDto :: new);
    }

    @PostMapping("/write/comment")
    public void writeComment(@RequestBody PostCommentDto commentDto){
        savePostService.saveComment(commentDto);
        log.info("댓글 저장");
    }

}
