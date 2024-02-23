package com.example.reactmapping.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    @JoinColumn(name = "memberId")
    private Member member;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="image_id")
    private Image image;
}
