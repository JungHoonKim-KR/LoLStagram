package com.example.reactmapping.dto;

import com.example.reactmapping.entity.PostComment;
import lombok.Builder;

@Builder
public class PostCommentDto {
    public Long postId;
    public String writerId;
    public String comment;
    public static PostCommentDto entityToDto(PostComment postComment){
        return PostCommentDto.builder()
                .postId(postComment.getId())
                .writerId(postComment.getWriterId())
                .comment(postComment.getComment())
                .build();
    }
}
