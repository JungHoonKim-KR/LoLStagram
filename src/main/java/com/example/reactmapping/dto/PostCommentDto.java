package com.example.reactmapping.dto;

import com.example.reactmapping.entity.PostComment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostCommentDto {
    public Long postId;
    public Long writeId;
    public String writerName;
    public String comment;
    public static PostCommentDto entityToDto(PostComment postComment){
        return PostCommentDto.builder()
                .postId(postComment.getId())
                .writerName(postComment.getWriterName())
                .comment(postComment.getComment())
                .build();
    }
}
