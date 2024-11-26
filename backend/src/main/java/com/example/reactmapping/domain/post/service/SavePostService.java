package com.example.reactmapping.domain.post.service;

import com.example.reactmapping.domain.member.entity.Member;
import com.example.reactmapping.domain.member.service.MemberService;
import com.example.reactmapping.domain.post.entity.Post;
import com.example.reactmapping.domain.post.entity.PostComment;
import com.example.reactmapping.domain.post.dto.PostCommentDto;
import com.example.reactmapping.domain.post.dto.PostDto;
import com.example.reactmapping.global.exception.AppException;
import com.example.reactmapping.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class SavePostService {
    private final PostService postService;
    private final MemberService memberService;

    public void savePost(PostDto postDto){
        Member findMember = memberService.findMemberById(postDto.getMemberId());
        Post post = createPost(postDto, findMember);
        postService.savePost(post);
    }
    public void saveComment(PostCommentDto postCommentDto){
        Post post = postService.findPostById(postCommentDto.postId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTFOUND, "게시글을 찾지 못했습니다."));
        Member writer = memberService.findMemberById(postCommentDto.writeId);

        setPostComment(postCommentDto, writer, post);
        postService.savePost(post);
    }

    private Post createPost(PostDto postDto, Member member){
        Post post = Post.builder()
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .createTime(LocalDateTime.now())
                .build();
        post.setMember(member);
        return post;
    }
    private static void setPostComment(PostCommentDto postCommentDto, Member writer, Post post) {

        PostComment postComment = PostComment.builder()
                .writer(writer)
                .comment(postCommentDto.comment)
                .build();
        postComment.setPost(post);
    }
}
