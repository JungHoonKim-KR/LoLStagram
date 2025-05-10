package com.example.reactmapping.domain.member.entity;

import com.example.reactmapping.domain.lol.summonerInfo.entity.SummonerInfo;
import com.example.reactmapping.domain.lol.summonerInfo.service.SummonerInfoService;
import com.example.reactmapping.domain.post.entity.Post;
import com.example.reactmapping.domain.post.entity.PostComment;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
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
    // member와 summonerInfo는 특이한 구조임
    // summonerInfo는 여러 member와 연관될 수 있지만 조회는 하지 않음.
    // 즉 member만 summonerInfo를 조회하기 때문에 cascade 설정을 연관관계의 주인인 member에 하게 됐음.
    // 단 ALL을 하게 될 경우 member 삭제 시 summoner까지 같이 삭제되면 데이터 무결성에 어긋남. (member가 "다"의 관계이기 때문에 다른 member에 영향이 감)
    @Setter
    @Schema(description = "소환사 아이디")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "summoner_id")
    private SummonerInfo summonerInfo;

    @OneToMany(mappedBy = "member",cascade = CascadeType.ALL)
    private List<Post> postList = new ArrayList<>();
    @OneToMany(mappedBy = "writer", cascade = CascadeType.ALL)
    private List<PostComment> commentList = new ArrayList<>();

    @Builder
    public Member(String emailId, String password, String username, String role, SummonerInfo summonerInfo) {
        this.emailId = emailId;
        this.password = password;
        this.username = username;
        this.role = role;
        this.summonerInfo = summonerInfo;
    }


    public void setOauthInfo(String emailId, String username){
        this.emailId = emailId;
        this.username = username;
    };

    public void updateSummonerInfo(SummonerInfo summonerInfo){
        this.summonerInfo = summonerInfo;
    }

}
