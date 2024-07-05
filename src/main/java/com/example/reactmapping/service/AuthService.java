package com.example.reactmapping.service;

import com.example.reactmapping.config.jwt.JwtService;
import com.example.reactmapping.config.jwt.JwtUtil;
import com.example.reactmapping.config.jwt.TokenDto;
import com.example.reactmapping.dto.*;
import com.example.reactmapping.entity.*;
import com.example.reactmapping.exception.AppException;
import com.example.reactmapping.exception.ErrorCode;
import com.example.reactmapping.norm.ImageType;
import com.example.reactmapping.norm.LOL;
import com.example.reactmapping.entity.Image;
import com.example.reactmapping.norm.Token;
import com.example.reactmapping.object.MostChampion;
import com.example.reactmapping.repository.*;
import com.example.reactmapping.service.LoL.LoLService;
import com.example.reactmapping.utils.CookieUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final MemberRepository memberRepository;

    private final SummonerInfoRepository summonerInfoRepository;
    private final MatchRepository matchRepository;
    private final MatchService matchService;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final LoLService loLService;
    private final ImgService imgService;
    private final ImageRepository imageRepository;
    private final EntityManager entityManager;
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;

    public Member join(JoinDTO dto) throws IOException {
        // 회원 아이디가 이미 존재하는지
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
            Image image = imgService.createImg(dto.getImg(), member.getEmailId(), null, ImageType.ProfileType.name());
            member = member.toBuilder().profileImg(image.getFileUrl()).build();
            memberRepository.save(member);
            imageRepository.save(image);
        }
        else {
            memberRepository.save(member);
        }
        return member;
    }

    public LoginResponseDto login(HttpServletResponse response, String requestEmail, String requestPassword,
                                  Pageable pageable,String type) throws JsonProcessingException {
        Member member = getMemberByEmail(requestEmail);
        verifyPassword(requestPassword, member);
        SummonerInfo summonerInfo = getSummonerInfo(member);
        List<MatchInfoDto> matchList = matchService.getMatchList(pageable, type,summonerInfo.getSummonerId()).getMatchInfoDtoList();
        TokenDto tokenDto = jwtService.generateToken(requestEmail);
        response.addCookie(cookieUtil.createCookie(Token.TokenName.refreshToken,tokenDto.getRefreshToken()));
        return getLoginResponseDto(response, member, tokenDto.getAccessToken(), summonerInfo, matchList);
    }

    public LoginResponseDto socialLogin(HttpServletResponse response, String accessToken,Pageable pageable,String type){
        Member member = getMemberByEmail(jwtUtil.getUserEmail(accessToken));
        SummonerInfo summonerInfo = getSummonerInfo(member);
        List<MatchInfoDto> matchList = matchService.getMatchList(pageable, type,summonerInfo.getSummonerId()).getMatchInfoDtoList();
        return getLoginResponseDto(response, member, accessToken, summonerInfo, matchList);
    }
   
    private SummonerInfo getSummonerInfo(Member member) {
        Optional<SummonerInfo> optionalSummonerInfo = summonerInfoRepository.findBySummonerId(member.getSummonerId());
        SummonerInfo summonerInfo;
        if(optionalSummonerInfo.isPresent())
            summonerInfo = optionalSummonerInfo.get();
        else throw new AppException(ErrorCode.NOTFOUND,"소환사 정보를 찾을 수 없습니다.");
        return summonerInfo;
    }
    

    private void verifyPassword(String requestPassword, Member member) {
            if (!bCryptPasswordEncoder.matches(requestPassword, member.getPassword()))
                throw new AppException(ErrorCode.ACCESS_ERROR, "비밀번호가 일치하지 않습니다.");
    }


    private Member getMemberByEmail(String requestEmail) {
        Member member;
        Optional<Member> findMember = memberRepository.findMemberByEmailId(requestEmail);
        if (findMember.isPresent()) {
            member = findMember.get();
        } else throw new AppException(ErrorCode.NOTFOUND, requestEmail + "는 존재하지 않습니다.");
        return member;
    }

    private LoginResponseDto getLoginResponseDto(HttpServletResponse response, Member member, String accessToken, SummonerInfo summonerInfo, List<MatchInfoDto> matchList) {
        MemberDto memberDto = new MemberDto(member.getId(), member.getEmailId(), member.getUsername(), member.getProfileImg());

        LoginResponseDto loginResponseDto = LoginResponseDto.builder()
                .accessToken(accessToken)
                .username(member.getUsername())
                .summonerInfoDto(SummonerInfoDto.entityToDto(summonerInfo))
                .memberDto(memberDto)
                .matchInfoDtoList(matchList)
                .build();
        return loginResponseDto;
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
        if(compare.getResult() != LOL.gameCount-1) {
        //최신 상태가 아니라면
            CreateSummonerInfoDto summonerInfoAndMatchList = loLService.createSummonerInfo(riotGameName, tag, summonerProfile, 0, LOL.gameCount);
            summonerProfile = summonerInfoAndMatchList.getSummonerInfo();
            List<MatchInfo> matchInfoList = summonerInfoAndMatchList.getMatchInfo();

            // 신규 가입이 아닌 경우 기존 경기에 대한 수정작업 진행
            if(compare.getResult()!=-1){
                List<MatchInfo> originMatchList = compare.getMatchInfoList();

                // 중첩된 개수를 구한다.
                int newGameCount = LOL.gameCount - compare.getResult();
                for(int i = LOL.gameCount-1, j = 0; j<newGameCount;i--, j++){
                    MatchInfo matchInfo = matchInfoList.get(j);
                    //originMatchList 정보를 일부 활용하기 때문에 예외적으로 Repo에 직접 접근 허용
                    matchRepository.updateAll(matchInfo.getMatchId(),matchInfo.getGameStartTimestamp(),matchInfo.getKills()
                            ,matchInfo.getDeaths(),matchInfo.getAssists(),matchInfo.getKda(),matchInfo.getChampionName(),matchInfo.getMainRune(),matchInfo.getSubRune()
                            ,matchInfo.getItemList(),matchInfo.getSummonerSpellList(),matchInfo.getResult()
                    ,originMatchList.get(i).getMatchId());
                }
            }
            //mostchampion 설정
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
