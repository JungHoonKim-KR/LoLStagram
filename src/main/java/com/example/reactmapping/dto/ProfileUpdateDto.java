package com.example.reactmapping.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileUpdateDto {
    private Long id;
    private String summonerName;
    private String summonerTag;
    private MultipartFile img;

}
