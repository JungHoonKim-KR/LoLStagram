package com.example.reactmapping.domain.post.controller;

import com.example.reactmapping.domain.member.domain.Member;
import com.example.reactmapping.domain.member.service.MemberService;
import com.example.reactmapping.domain.post.dto.PostCommentDto;
import com.example.reactmapping.domain.post.dto.PostDto;
import com.example.reactmapping.domain.post.dto.PostResultDto;
import com.example.reactmapping.domain.post.service.GetPostService;
import com.example.reactmapping.domain.post.service.SavePostService;
import com.example.reactmapping.global.exception.AppException;
import com.example.reactmapping.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/post")
@Slf4j
public class PostController {
    private final SavePostService savePostService;
    private final GetPostService getPostService;
    private final MemberService memberService;
    @PostMapping("/write/post")
    public void writePost(@RequestPart("postDto") PostDto postDto, @RequestPart(name = "image", required = false)MultipartFile image) throws IOException {
        if(image!=null)
            postDto.setServerImage(image);
        savePostService.savePost(postDto);
        log.info("게시글 등록 완료");
    }
    @GetMapping("/postList")
    public PostResultDto getPostList(@PageableDefault(size = 3,direction = Sort.Direction.DESC)  Pageable pageable) {
        log.info("게시글 불러오기");
        return getPostService.getPostList(pageable);
    }
    @PostMapping("/write/comment")
    public void writeComment(@RequestBody PostCommentDto commentDto){
        savePostService.saveComment(commentDto);
        log.info("댓글 저장");
    }

}
