package com.example.reactmapping.domain.post.dto;

import com.example.reactmapping.domain.post.entity.Post;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Data
@NoArgsConstructor
public class PostDto {
    private Long postId;
    private String title;
    private String content;
    private Long memberId;
    private String memberName;
    private List<PostCommentDto> commentList;

    @Builder
    public PostDto(Post post) {
        this.postId = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.memberId = post.getMember().getId();
        this.memberName = post.getMember().getUsername();
        this.commentList = Optional.ofNullable(post.getCommentList())
                .map(comment -> comment.stream().map(PostCommentDto::new).collect(Collectors.toList()))
                .orElse(new ArrayList<>());
    }
}
