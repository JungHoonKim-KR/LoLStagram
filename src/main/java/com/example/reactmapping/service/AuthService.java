package com.example.reactmapping.service;

import com.example.reactmapping.config.jwt.JwtService;
import com.example.reactmapping.config.jwt.TokenDto;
import com.example.reactmapping.dto.JoinDTO;
import com.example.reactmapping.dto.LoginRequestDto;
import com.example.reactmapping.dto.LoginResponseDto;
import com.example.reactmapping.entity.AccessToken;
import com.example.reactmapping.entity.Member;
import com.example.reactmapping.entity.RefreshToken;
import com.example.reactmapping.exception.AppException;
import com.example.reactmapping.exception.ErrorCode;
import com.example.reactmapping.repository.BlackListRepository;
import com.example.reactmapping.repository.MemberRepository;
import com.example.reactmapping.repository.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BlackListRepository blackListRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public Member join(JoinDTO dto) {
        if (memberRepository.findMemberByEmailId(dto.getEmailId()).isPresent()) {
            throw new AppException(ErrorCode.DUPLICATED, dto.getEmailId() + "는 이미 존재합니다.");

        } else {
            Member member = Member.builder()
                    .emailId(dto.getEmailId())
                    .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                    .username(dto.getUsername())
                    .role("ROLE_MEMBER")
                    .build();
            memberRepository.save(member);
            return member;
        }
    }

    public LoginResponseDto login(HttpServletResponse response, String requestEmail, String requestPassword,String authenticationCode) {
        Member member = null;
        Optional<Member> findMember = memberRepository.findMemberByEmailId(requestEmail);
        //존재하는 회원인지
        if (findMember.isPresent()) {
            member = findMember.get();
            //비밀번호 확인
            if(authenticationCode == null) {
                if (!bCryptPasswordEncoder.matches(requestPassword, member.getPassword())) {
                    throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
                }

            }
        }
        else throw new AppException(ErrorCode.NOTFOUND, requestEmail+ "논 존재하지 않습니다.");
        //토큰 발급
        TokenDto tokenDto = jwtService.login(requestEmail);

        //존재하는 회원이라면 refresh 토큰 확인
        Optional<RefreshToken> findRefreshToken = refreshTokenRepository.findRefreshTokenByEmailId(member.getEmailId());
        //refresh 토큰이 존재 하는지
        RefreshToken refreshToken;
        if (findRefreshToken.isPresent()) {
            //존재한다면 갱신
            refreshToken = findRefreshToken.get().updateToken(tokenDto.getRefreshToken());
        } else {
            refreshToken = new RefreshToken(tokenDto.getUserEmail(), tokenDto.getRefreshToken());
        }
        refreshTokenRepository.save(refreshToken);

        //반환 값
        LoginResponseDto loginResponseDto = LoginResponseDto.builder().
                tokenDto(tokenDto)
                .username(member.getUsername())
                .build();

        setHeader(response, loginResponseDto);


        return loginResponseDto;

    }

    public void logout(String emailId, String accessToken, String refreshToken) {
        Optional<RefreshToken> refreshTokenByRefreshToken = refreshTokenRepository.findRefreshTokenByRefreshToken(refreshToken);
        if (refreshTokenByRefreshToken.isPresent()) {
            log.info("refreshToken 존재");
            refreshTokenRepository.deleteRefreshTokenByRefreshToken(refreshToken);
            log.info("refreshToken 삭제");

        }
        Optional<AccessToken> accessTokenByEmailId = blackListRepository.findAccessTokenByEmailId(emailId);
        if (accessTokenByEmailId.isPresent()) {
            AccessToken accessToken1 = accessTokenByEmailId.get();
        }
    }

    //응답에 access, refresh 토큰을 부여함
    //이건 헤더에 직접적으로 토큰을 부여하는게 아니라 프론트엔드에게 전달할 응답값에 토큰을 넣는 것임.
    //request : 클라이언트의 요청값, response: 백 -> 프론트 전달값
    private void setHeader(HttpServletResponse response, LoginResponseDto loginResponseDto) {
        //클라이언트에 다음 header에 접근할 수 있게 함
//        response.setHeader("Access-Control-Expose-Headers", "ACCESS,REFRESH");
        response.setHeader("ACCESS", loginResponseDto.getTokenDto().getAccessToken());
        response.setHeader("REFRESH", loginResponseDto.getTokenDto().getRefreshToken());
    }


}
