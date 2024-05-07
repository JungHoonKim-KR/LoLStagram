package com.example.reactmapping.controller;

import com.example.reactmapping.dto.PostResultDto;
import com.example.reactmapping.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
public class TestController {
    private final PostService postService;
    @GetMapping("/test")
    public PostResultDto getPostList(@PageableDefault(size = 2,direction = Sort.Direction.DESC) Pageable pageable) {
        return postService.getPostList(pageable);
    }
}
