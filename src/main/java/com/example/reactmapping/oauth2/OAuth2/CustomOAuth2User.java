package com.example.reactmapping.oauth2.OAuth2;

import com.example.reactmapping.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {
    private final Member member;
    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return member.getRole();
            }
        });
        return collection;
    }
    public String getEmail(){
        return member.getEmailId();
    }
    @Override
    public String getName() {
        return member.getUsername();
    }


}
