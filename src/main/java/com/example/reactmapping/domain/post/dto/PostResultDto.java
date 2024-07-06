package com.example.reactmapping.domain.post.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostResultDto {
    private List<PostDto> postDtoList= new ArrayList<>();
    private Boolean isLast;
}
