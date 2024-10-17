package com.example.reactmapping.domain.post.dto;

import com.example.reactmapping.domain.member.entity.Member;
import com.example.reactmapping.domain.post.entity.Post;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    private Long postId;
    private String title;
    private String content;
    private Long memberId;
    private String memberName;
    private List<PostCommentDto> commentList;
    private Boolean isLast;
    private Member member;
  public static PostDto entityToDto(Post post){
      List<PostCommentDto> postCommentDtos = post.getCommentList() != null
              ? post.getCommentList().stream().map(PostCommentDto::entityToDto).collect(Collectors.toList())
              : new ArrayList<>();
      PostDto build = PostDto.builder()
              .postId(post.getId())
              .title(post.getTitle())
              .content(post.getContent())
              .memberId(post.getMember().getId())
              .memberName(post.getMember().getUsername())
              .commentList(postCommentDtos)
              .build();

      return build;
  }
}
