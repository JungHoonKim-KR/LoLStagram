package com.example.reactmapping.domain.matchInfo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class MatchInfoResultDto {
    public List<MatchInfoDto> matchInfoDtoList;
    public Boolean isLast;
    public String type;
}
