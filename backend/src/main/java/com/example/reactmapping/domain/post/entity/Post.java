package com.example.reactmapping.domain.post.entity;

import com.example.reactmapping.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
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
    @Setter
    private String imageUrl;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostComment> commentList = new ArrayList<>();

    @Builder
    public Post(String title, String content, LocalDateTime createTime, String imageUrl) {
        this.title = title;
        this.content = content;
        this.createTime = createTime;
        this.imageUrl = imageUrl;
    }
    public void setMember(Member member) {
        this.member = member;
        member.getPostList().add(this);
    }


}
