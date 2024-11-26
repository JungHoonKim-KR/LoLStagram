package com.example.reactmapping.oauth2;

import com.example.reactmapping.oauth2.OAuth2.CustomOAuth2User;
import com.example.reactmapping.oauth2.OAuth2.GoogleResponse;
import com.example.reactmapping.oauth2.OAuth2.NaverResponse;
import com.example.reactmapping.domain.member.entity.Member;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomOauth2UserService extends DefaultOAuth2UserService {
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info(String.valueOf(oAuth2User));

        String registerId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = getOAuth2Response(registerId, oAuth2User);
        if (oAuth2Response == null) return null;
        else return new CustomOAuth2User(getMember(oAuth2Response));
    }

    private Member getMember(OAuth2Response oAuth2Response) {
        Member member = new Member();
        member.setOauthInfo(oAuth2Response.getEmail(), oAuth2Response.getName());
        log.info(member.getEmailId());
        log.info(member.getUsername());
        return member;
    }

    private @Nullable OAuth2Response getOAuth2Response(String registerId, OAuth2User oAuth2User) {
        OAuth2Response oAuth2Response;
        if (registerId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        } else if (registerId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else {
            return null;
        }
        return oAuth2Response;
    }
}
