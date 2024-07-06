package com.example.reactmapping.domain.post.domain;

import com.example.reactmapping.domain.Image.domain.Image;
import com.example.reactmapping.domain.member.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createTime=LocalDateTime.now();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="image_id")
    private Image image;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostComment> commentList = new ArrayList<>();

    public void setMember(Member member) {
        this.member = member;
        member.getPostList().add(this);
    }
//    public void addComment(PostComment comment){
//        if(commentList == null)
//            commentList=new ArrayList<>();
//        PostComment updatedComment = comment.toBuilder().post(this).build();
//        commentList.add(updatedComment);
//    }
}
