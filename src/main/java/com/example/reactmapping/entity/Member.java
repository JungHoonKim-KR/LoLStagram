package com.example.reactmapping.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memberId")
    private Long id;
    @Schema(description = "회원 이메일")
    private String emailId;
    @Schema(description = "회원 비밀번호")
    private String password;
    @Schema(description = "회원 이름")
    private String username;
    @Schema(description = "권한")
    private String role;
    @Schema(description = "라이엇 닉네임")
    private String riotIdGameName;
    @Schema(description = "라이엇 태그")
    private String riotIdTagline;
    @Schema(description = "소환사 아이디")
    private String summonerId;
    @Schema(description = "프로필 사진")
    @Nullable
    private String profileImg;
    @OneToMany(mappedBy = "member",cascade = CascadeType.ALL)
    private List<Post> postList = new ArrayList<>();

    public void addPostList(Post post){
        this.postList.add(post);
        post.toBuilder().member(this);
    }

}
