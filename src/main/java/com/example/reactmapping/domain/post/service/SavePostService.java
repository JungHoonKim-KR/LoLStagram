package com.example.reactmapping.domain.post.service;

import com.example.reactmapping.domain.Image.service.ImageCreateService;
import com.example.reactmapping.domain.member.domain.Member;
import com.example.reactmapping.domain.member.service.MemberService;
import com.example.reactmapping.domain.post.domain.Post;
import com.example.reactmapping.domain.post.domain.PostComment;
import com.example.reactmapping.domain.post.dto.PostCommentDto;
import com.example.reactmapping.domain.post.dto.PostDto;
import com.example.reactmapping.global.exception.AppException;
import com.example.reactmapping.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class SavePostService {
    private final PostService postService;
    private final ImageCreateService imageCreateService;
    private final MemberService memberService;

    public void savePost(PostDto postDto) throws IOException {
        Member findMember = memberService.findMemberById(postDto.getMemberId());
        Post post = createPost(postDto, findMember);
        postService.savePost(post);
    }
    public void saveComment(PostCommentDto postCommentDto){
        Post post = postService.findPostById(postCommentDto.postId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTFOUND, "게시글을 찾지 못했습니다."));
        Member writer = memberService.findMemberById(postCommentDto.writeId);

        createPostComment(postCommentDto, writer, post);
        postService.savePost(post);
    }

    private Post createPost(PostDto postDto, Member member) throws IOException {
        Post post = Post.builder()
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .createTime(LocalDateTime.now())
                .build();
        if (postDto.getServerImage() != null) {
            String imageUrl = imageCreateService.createImage(postDto.getServerImage());
            post.setImageUrl(imageUrl);
        }
        post.setMember(member);
        return post;
    }
    private static void createPostComment(PostCommentDto postCommentDto, Member writer, Post post) {
        PostComment postComment = new PostComment().toBuilder()
                .writer(writer)
                .comment(postCommentDto.comment)
                .build();
        postComment.setPost(post);
    }
}
