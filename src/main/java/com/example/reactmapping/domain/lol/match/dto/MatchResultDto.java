package com.example.reactmapping.domain.lol.match.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class MatchResultDto {
    public List<MatchDto> MatchDtoList;
    public Boolean isLast;
    public String type;
}
