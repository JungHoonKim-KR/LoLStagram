package com.example.reactmapping.domain.post.controller;

import com.example.reactmapping.domain.post.service.PostService;
import com.example.reactmapping.domain.post.dto.PostCommentDto;
import com.example.reactmapping.domain.post.dto.PostDto;
import com.example.reactmapping.domain.post.dto.PostResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/post")
public class PostController {
    private final PostService postService;

    @PostMapping("/write/post")
    public void writePost(@RequestPart("postDto") PostDto postDto, @RequestPart(name = "image", required = false)MultipartFile image) throws IOException {
        if(image!=null)
            postDto = postDto.toBuilder().serverImage(image).build();
        postService.savePost(postDto);
    }
    @GetMapping("/postList")
    public PostResultDto getPostList(@PageableDefault(size = 3,direction = Sort.Direction.DESC)  Pageable pageable) {
        return postService.getPostList(pageable);
    }
    @PostMapping("/write/comment")
    public void writeComment(@RequestBody PostCommentDto commentDto){
        postService.saveComment(commentDto);
    }

}
