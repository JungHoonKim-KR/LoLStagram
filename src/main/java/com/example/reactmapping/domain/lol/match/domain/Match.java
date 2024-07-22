package com.example.reactmapping.domain.lol.match.domain;

import com.example.reactmapping.StringListConverter;
import com.example.reactmapping.domain.lol.summonerInfo.domain.SummonerInfo;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.Persistable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "matches")
public class Match implements Persistable<String> {
    @Id
    private String matchId;
    private Long gameStartTimestamp;
    private Long kills;
    private Long deaths;
    private Long assists;
    private String kda;
    private String championName;
    private Long mainRune;
    private Long subRune;
    private String gameType;
    @Convert(converter = StringListConverter.class)
    private List<Integer> itemList;
    @Convert(converter = StringListConverter.class)
    private List<Integer> summonerSpellList;
    private String result;
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "summoner_id")
    @JsonBackReference
    private SummonerInfo summonerInfo;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
    @Override
    public boolean isNew() {
        return createTime == null;
    }

    public void updateSummonerInfo(SummonerInfo summonerInfo) {
        this.summonerInfo = summonerInfo;
    }
    // ID 필드에 대한 getter
    @Override
    public String getId() {
        return matchId;
    }

}
