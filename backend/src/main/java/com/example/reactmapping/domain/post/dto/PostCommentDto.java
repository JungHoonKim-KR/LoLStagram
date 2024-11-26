package com.example.reactmapping.domain.post.dto;

import com.example.reactmapping.domain.post.entity.PostComment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@NoArgsConstructor
@Getter
public class PostCommentDto {
    public Long postId;
    public Long writeId;
    public String writerName;
    public String comment;

    public PostCommentDto(PostComment postComment) {
        this.postId = postComment.getId();
        this.writeId = postComment.getWriter().getId();
        this.writerName = postComment.getWriter().getUsername();
        this.comment = postComment.getComment();
    }


}
