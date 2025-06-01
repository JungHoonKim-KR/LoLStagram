package com.example.reactmapping.domain.Image.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private long id;
    private String type;
    private String name;
    private String url;

    public Image(String type, String name, String url) {
        this.type = type;
        this.name = name;
        this.url = url;
    }
}
