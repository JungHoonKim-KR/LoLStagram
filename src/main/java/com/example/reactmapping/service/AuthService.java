package com.example.reactmapping.service;

import com.example.reactmapping.config.jwt.JwtService;
import com.example.reactmapping.config.jwt.TokenDto;
import com.example.reactmapping.dto.*;
import com.example.reactmapping.entity.*;
import com.example.reactmapping.exception.AppException;
import com.example.reactmapping.exception.ErrorCode;
import com.example.reactmapping.norm.ImageType;
import com.example.reactmapping.norm.LOL;
import com.example.reactmapping.entity.Image;
import com.example.reactmapping.object.MostChampion;
import com.example.reactmapping.object.RefreshToken;
import com.example.reactmapping.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SummonerInfoRepository summonerInfoRepository;
    private final MatchRepository matchRepository;
    private final MatchService matchService;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final LoLService loLService;
    private final ImgService imgService;
    private final ImageRepository imageRepository;
    private final EntityManager entityManager;

    public Member join(JoinDTO dto) throws IOException {
        if (memberRepository.findMemberByEmailId(dto.getEmailId()).isPresent()) {
            throw new AppException(ErrorCode.DUPLICATED, dto.getEmailId() + "는 이미 존재합니다.");
        }
        CallSummonerInfoResponse callSummonerInfoResponse = callSummonerInfo(dto.getSummonerName(), dto.getSummonerTag());

        Member member = Member.builder()
                .emailId(dto.getEmailId())
                .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                .username(dto.getUsername())
                .role("ROLE_MEMBER")
                .riotIdGameName(dto.getSummonerName())
                .riotIdTagline(dto.getSummonerTag())
                .summonerId(callSummonerInfoResponse.getSummonerId())
                .build();
        if(dto.getImg() != null) {
            Image image = imgService.createImg(dto.getImg(), member.getEmailId(), null, String.valueOf(ImageType.ProfileType));
            member = member.toBuilder().profileImg(image.getFileUrl()).build();
            memberRepository.save(member);
            imageRepository.save(image);
        }
        else {
            memberRepository.save(member);
        }
        return member;
    }

    public LoginResponseDto login(HttpSession httpSession, HttpServletResponse response, String requestEmail, String requestPassword, String authenticationCode, Pageable pageable,String type) throws JsonProcessingException {
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
        } else throw new AppException(ErrorCode.NOTFOUND, requestEmail + "는 존재하지 않습니다.");
        //토큰 발급
        TokenDto tokenDto = jwtService.login(requestEmail);
        httpSession.setAttribute(tokenDto.getAccessToken(),requestEmail);
        log.info("로그인 유저: " + httpSession.getAttribute(tokenDto.getAccessToken()));
        Cookie sessionCookie = new Cookie("JSESSIONID", httpSession.getId());
        response.addCookie(sessionCookie);
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
        SummonerInfo summonerInfo = summonerInfoRepository.findBySummonerId(member.getSummonerId()).get();
        //Get MatchList
        List<MatchInfoDto> matchList = matchService.getMatchList(pageable, type,summonerInfo.getSummonerId()).getMatchInfoDtoList();
        MemberDto memberDto = new MemberDto(member.getId(),member.getEmailId(), member.getUsername(), member.getProfileImg());

        //반환 값
        LoginResponseDto loginResponseDto = LoginResponseDto.builder()
                .accessToken(tokenDto.getAccessToken())
                .username(member.getUsername())
                .summonerInfoDto(SummonerInfoDto.entityToDto(summonerInfo))
                .memberDto(memberDto)
                .matchInfoDtoList(matchList)
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
    @Transactional
    public CallSummonerInfoResponse callSummonerInfo(String riotGameName, String tag) throws JsonProcessingException {
        String puuId, summonerId;
        // riot 닉네임이 존재하는지
        puuId = loLService.callPuuId(riotGameName,tag);
        log.info("find puuId: "+ puuId);

        summonerId = loLService.callSummonerId(puuId);
        //리그 정보 가져오기
        SummonerInfo summonerProfile = loLService.callSummonerProfile(summonerId,tag);
        summonerProfile = summonerProfile.toBuilder().puuId(puuId).summonerId(summonerId).summonerName(riotGameName).build();
        //최근 전적 가져오기
        CompareDto compare = loLService.compare(puuId, summonerId);

        //9라면 이미 최신 상태임
        if(compare.getResult() != LOL.INFO.getGameCount()-1) {
        //최신 상태가 아니라면
            CreateSummonerInfoDto summonerInfoAndMatchList = loLService.createSummonerInfo(riotGameName, tag, summonerProfile, 0, LOL.INFO.getGameCount());
            summonerProfile = summonerInfoAndMatchList.getSummonerInfo();
            List<MatchInfo> matchInfoList = summonerInfoAndMatchList.getMatchInfo();

            // 신규 가입이 아닌 경우 기존 경기에 대한 수정작업 진행
            if(compare.getResult()!=-1){
                List<MatchInfo> originMatchList = compare.getMatchInfoList();

                // 중첩된 개수를 구한다.
                int newGameCount = LOL.INFO.getGameCount() - compare.getResult();

                for(int i = LOL.INFO.getGameCount()-1, j = 0; j<newGameCount;i--, j++){
                    MatchInfo matchInfo = matchInfoList.get(j);
                    matchRepository.updateAll(matchInfo.getMatchId(),matchInfo.getGameStartTimestamp(),matchInfo.getKills()
                            ,matchInfo.getDeaths(),matchInfo.getAssists(),matchInfo.getKda(),matchInfo.getChampionName(),matchInfo.getMainRune(),matchInfo.getSubRune()
                            ,matchInfo.getItemList(),matchInfo.getSummonerSpellList(),matchInfo.getResult()
                    ,originMatchList.get(i).getMatchId());
                }
            }
            List<MostChampion> mostChampionList = loLService.calcMostChampion(matchInfoList);
            summonerProfile = summonerProfile.toBuilder().mostChampionList(mostChampionList).build();

            for (MatchInfo matchInfo : matchInfoList) {
                summonerProfile.addMatchInfo(matchInfo);
            }
            // 바로 저장
            if(compare.getResult()==-1){
                // match save -> dirty checking : summonerInfo save
                matchService.matchSaveAll(summonerProfile.getMatchList());
            }
            else{
                // 기존 회원이라면 이미 match, summonerInfo가 영속성 컨텍스트에 포함됨
                // 이 때 신규회원과 통일시키기 위해 match를 update하는 방식을 사용하면 구현이 복잡함.
                // 그래서 summonerInfo를 update하여 match update를 JPA에게 맡김
                // 영속성 컨텍스트에 저장된 summonerInfo 객체를 추적하기 어려워 직접 찾아서 사용
                SummonerInfo existingSummonerInfo = entityManager.find(SummonerInfo.class, summonerProfile.getId());
                if (existingSummonerInfo != null) {
                    existingSummonerInfo.update(summonerProfile);
                    // 명시적으로 병합하여 변경 사항을 반영
                    entityManager.merge(existingSummonerInfo);
                } else {
                    log.warn("SummonerInfo is not in the persistence context.");
                    throw new AppException(ErrorCode.NOTFOUND,"SummonerInfo is not in the persistence context.");
                }
            }
        }
        return new CallSummonerInfoResponse(summonerProfile,summonerId);
    }

    public void updateProfile(ProfileUpdateDto profileUpdateDto) throws IOException {
        Member member = memberRepository.findMemberById(profileUpdateDto.getId()).get();
        CallSummonerInfoResponse callSummonerInfoResponse = callSummonerInfo(profileUpdateDto.getSummonerName(), profileUpdateDto.getSummonerTag());

        member = member.toBuilder()
                .riotIdGameName(profileUpdateDto.getSummonerName())
                .riotIdTagline(profileUpdateDto.getSummonerTag())
                .summonerId(callSummonerInfoResponse.getSummonerId())
                .build();
        memberRepository.save(member);
    }

}
