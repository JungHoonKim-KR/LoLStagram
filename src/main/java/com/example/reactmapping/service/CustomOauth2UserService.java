package com.example.reactmapping.service;

import com.example.reactmapping.dto.OAuth2.CustomOAuth2User;
import com.example.reactmapping.dto.OAuth2.GoogleResponse;
import com.example.reactmapping.dto.OAuth2.NaverResponse;
import com.example.reactmapping.oauth2.*;
import com.example.reactmapping.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOauth2UserService extends DefaultOAuth2UserService {
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info(String.valueOf(oAuth2User));
        String registerId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;

        if (registerId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        } else if (registerId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else {
            return null;
        }
        Member member = new Member();
        member = member.toBuilder()
                .emailId(oAuth2Response.getEmail())
                .username(oAuth2Response.getName())
                .build();
        log.info(member.getEmailId());
        log.info(member.getUsername());
        return new CustomOAuth2User(member);
    }
}
