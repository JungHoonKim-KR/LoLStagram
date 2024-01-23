package com.example.reactmapping.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AccessToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String emailId;
    @NotBlank
    private String accessToken;


    public AccessToken(String emailId, String accessToken) {
        this.emailId = emailId;
        this.accessToken = accessToken;
    }

    public AccessToken updateToken(String token) {
        this.accessToken = token;
        return this;
    }
}
