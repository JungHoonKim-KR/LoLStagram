package com.example.reactmapping.domain.member.controller;

import com.example.reactmapping.domain.lol.summonerInfo.riotApi.GetSummonerInfoWithApi;
import com.example.reactmapping.domain.member.entity.Member;
import com.example.reactmapping.domain.member.dto.JoinDTO;
import com.example.reactmapping.domain.member.service.JoinService;
import com.example.reactmapping.domain.member.service.MemberService;
import com.example.reactmapping.global.norm.Token;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final JoinService joinService;
    private final GetSummonerInfoWithApi getSummonerInfoWithApi;
    private final MemberService memberService;
    @Operation(summary = "회원가입", description = "새로운 회원 등록")
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody JoinDTO dto) throws IOException {
        String puuId = getSummonerInfoWithApi.getPuuId(dto.getSummonerName(), dto.getSummonerTag());
        Member member = joinService.join(dto, puuId);
        log.info("회원가입 완료");
        return ResponseEntity.ok().body(member);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest httpServletRequest,
                                         @CookieValue(name = Token.TokenName.refreshToken) String refreshToken) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String accessToken = authentication.getPrincipal().toString();

        memberService.logout(accessToken, refreshToken);
        httpServletRequest.getSession().invalidate();
        return ResponseEntity.ok("logout");
    }
}
