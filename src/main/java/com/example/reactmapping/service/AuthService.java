package com.example.reactmapping.service;

import com.example.reactmapping.config.jwt.JwtService;
import com.example.reactmapping.config.jwt.TokenDto;
import com.example.reactmapping.dto.JoinDTO;
import com.example.reactmapping.dto.LoginResponseDto;
import com.example.reactmapping.entity.LeagueInfo;
import com.example.reactmapping.entity.Member;
import com.example.reactmapping.entity.RefreshToken;
import com.example.reactmapping.exception.AppException;
import com.example.reactmapping.exception.ErrorCode;
import com.example.reactmapping.repository.LeagueInfoRepository;
import com.example.reactmapping.repository.RefreshTokenRepository;
import com.example.reactmapping.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final LeagueInfoRepository leagueInfoRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final LoLService loLService;

    public Member join(JoinDTO dto) {
        // 회원 아이디가 이미 존재하는지
        if (memberRepository.findMemberByEmailId(dto.getEmailId()).isPresent()) {
            throw new AppException(ErrorCode.DUPLICATED, dto.getEmailId() + "는 이미 존재합니다.");
        }
        String puuId, summonerId;
        // riot 닉네임이 존재하는지
        try {
            puuId = loLService.findpuuId(dto.getRiotIdGameName(), dto.getRiotIdTagline());
            // 이후 result를 이용한 로직 작성
        } catch (RuntimeException e) {
            // findpuuId 메서드에서 발생한 에러 처리
            throw new AppException(ErrorCode.NOTFOUND, "puuId를 찾을 수 없습니다.");
        }

        summonerId = loLService.findSummonerId(puuId);
        Member member = Member.builder()
                .emailId(dto.getEmailId())
                .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                .username(dto.getUsername())
                .role("ROLE_MEMBER")
                .riotIdGameName(dto.getRiotIdGameName())
                .riotIdTagline(dto.getRiotIdTagline())
                .summonerId(summonerId)
                .puuId(puuId)
                .build();
        memberRepository.save(member);
        return member;
    }

    public LoginResponseDto login(HttpServletResponse response, String requestEmail, String requestPassword, String authenticationCode) throws JsonProcessingException {
        Member member = null;
        Optional<Member> findMember = memberRepository.findMemberByEmailId(requestEmail);
        //존재하는 회원인지
        if (findMember.isPresent()) {
            member = findMember.get();
            //비밀번호 확인
            if (authenticationCode == null) {
                if (!bCryptPasswordEncoder.matches(requestPassword, member.getPassword())) {
                    throw new AppException(ErrorCode.ACCESS_ERROR, "비밀번호가 일치하지 않습니다.");
                }

            }
        } else throw new AppException(ErrorCode.NOTFOUND, requestEmail + "논 존재하지 않습니다.");
        //토큰 발급
        TokenDto tokenDto = jwtService.login(requestEmail);

        //존재하는 회원이라면 refresh 토큰 확인
        Optional<RefreshToken> findRefreshToken = refreshTokenRepository.findById(member.getEmailId(), "refreshToken");

        //refresh 토큰이 존재 하는지
        RefreshToken refreshToken;
        if (findRefreshToken.isPresent()) {
            //존재한다면 갱신
            refreshToken = findRefreshToken.get().updateToken(tokenDto.getRefreshToken());
        } else {
            //없다면 새로 생성
            refreshToken = new RefreshToken(tokenDto.getUserEmail(), tokenDto.getRefreshToken());
        }
        refreshTokenRepository.save(refreshToken);

        //최근 전적 가져오기
        Long avgOfWin = loLService.avgOfWin(member);
        //리그 정보 가져오기
        LeagueInfo leagueInfo = loLService.leagueInfo(member.getSummonerId());
        leagueInfo = leagueInfo.toBuilder().avgOfWin(avgOfWin).build();
        leagueInfoRepository.save(leagueInfo);

        //반환 값
        LoginResponseDto loginResponseDto = LoginResponseDto.builder().
                accessToken(tokenDto.getAccessToken())
                .username(member.getUsername())
                .leagueInfo(leagueInfo)
                .build();

        setHeader(response, loginResponseDto);
        return loginResponseDto;

    }


    //응답에 access 토큰 부여
    //이건 헤더에 직접적으로 토큰을 부여하는게 아니라 프론트엔드에게 전달할 응답값에 토큰을 넣는 것임.
    //request : 클라이언트의 요청값, response: 백 -> 프론트 전달값
    private void setHeader(HttpServletResponse response, LoginResponseDto loginResponseDto) {
        //클라이언트에 다음 header에 접근할 수 있게 함
//        response.setHeader("Access-Control-Expose-Headers", "ACCESS,REFRESH");
        response.setHeader("loginInfo", String.valueOf(loginResponseDto));

    }


}
