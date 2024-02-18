package com.example.reactmapping.controller;

import com.example.reactmapping.dto.PostDto;
import com.example.reactmapping.entity.Post;
import com.example.reactmapping.service.PostService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class WriteController {
    private final PostService postService;

    @PostMapping("/write")
    public void write(@RequestBody PostDto postDto) throws IOException {
        postService.save(postDto);
    }

    @GetMapping("/postList")
    public List<PostDto> getPostList() {
        return postService.getPostList();
    }
}
