package com.example.reactmapping.domain.Image.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExtractedImageFIle {
    private String filename;
    private String fileOriginName;
    private String fileExtension;
}
