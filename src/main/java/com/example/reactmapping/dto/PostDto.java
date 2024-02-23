package com.example.reactmapping.dto;

import com.example.reactmapping.entity.Image;
import com.example.reactmapping.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    private String title;
    private String content;
    private MultipartFile serverImg;
    private String frontImage;
    private Long memberId;
    private String memberName;

  public static PostDto entityToDto(Post post){
      return PostDto.builder()
              .title(post.getTitle())
              .content(post.getContent())
              .memberId(post.getMember().getId())
              .memberName(post.getMember().getUsername())
              .frontImage(post.getImage().getFileUrl())
              .build();
  }
}
