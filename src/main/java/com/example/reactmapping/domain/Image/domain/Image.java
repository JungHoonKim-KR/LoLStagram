package com.example.reactmapping.domain.Image.domain;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;
    @Nullable
    private Long objectId;
    @Nullable
    private String emailId;
    private String filename;
    private String fileOriginName;
    private String fileUrl;
    private String fileExtension;
    private String objectType;

}
