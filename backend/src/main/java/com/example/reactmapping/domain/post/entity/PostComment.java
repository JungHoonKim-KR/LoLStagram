package com.example.reactmapping.domain.post.entity;

import com.example.reactmapping.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class PostComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "postComment_id")
    private Long id;
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post")
    private Post post;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer")
    private Member writer;

    @Builder
    public PostComment(String comment, Post post, Member writer) {
        this.comment = comment;
        this.post = post;
        this.writer = writer;
    }

    public void setPost(Post post) {
        this.post = post;
        post.getCommentList().add(this);
    }
}
