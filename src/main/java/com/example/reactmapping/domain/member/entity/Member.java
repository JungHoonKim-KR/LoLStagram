package com.example.reactmapping.domain.member.entity;

import com.example.reactmapping.domain.lol.summonerInfo.entity.SummonerInfo;
import com.example.reactmapping.domain.post.entity.Post;
import com.example.reactmapping.domain.post.entity.PostComment;
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
    @Column(name = "member_id")
    private Long id;
    @Schema(description = "회원 이메일")
    private String emailId;
    @Schema(description = "회원 비밀번호")
    private String password;
    @Schema(description = "회원 이름")
    private String username;
    @Schema(description = "권한")
    private String role;
//    @Schema(description = "라이엇 닉네임")
//    private String riotIdGameName;
//    @Schema(description = "라이엇 태그")
//    private String riotIdTagline;
    // member와 summonerInfo는 특이한 구조임
    // summonerInfo는 여러 member와 연관될 수 있지만 조회는 하지 않음.
    // 즉 member만 summonerInfo를 조회하기 때문에 cascade 설정을 연관관계의 주인인 member에 하게 됐음.
    // 단 ALL을 하게 될 경우 member 삭제 시 summoner까지 같이 삭제되면 데이터 무결성에 어긋남. (member가 "다"의 관계이기 때문에 다른 member에 영향이 감)
    @Schema(description = "소환사 아이디")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "summoner_id")
    private SummonerInfo summonerInfo;
    @Setter
//    @Schema(description = "프로필 사진")
//    @Nullable
//    private String profileImage;
    @OneToMany(mappedBy = "member",cascade = CascadeType.ALL)
    private List<Post> postList = new ArrayList<>();
    @OneToMany(mappedBy = "writer", cascade = CascadeType.ALL)
    private List<PostComment> commentList = new ArrayList<>();

}
