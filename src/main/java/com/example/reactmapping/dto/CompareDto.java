package com.example.reactmapping.dto;

import com.example.reactmapping.entity.MatchInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class CompareDto {
    private int result;
    private List<MatchInfo> matchInfoList=new ArrayList<>();
}
