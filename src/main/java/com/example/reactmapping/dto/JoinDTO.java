package com.example.reactmapping.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JoinDTO {
    private String emailId;
    private String password;
    private String username;
    private String riotIdGameName;
    private String riotIdTagline;

}
