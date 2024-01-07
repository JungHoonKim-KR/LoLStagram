package com.example.reactmapping.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
//개별 수정을 위해 setter를 사용했다.
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Schema(description = "회원 이메일")
    private String emailId;
    @Schema(description = "회원 비밀번호")
    private String password;
    @Schema(description = "회원 이름")
    private String username;
    @Schema(description = "권한")
    private String role;

    public void updateName(String username){
        this.username=username;
    }

}
