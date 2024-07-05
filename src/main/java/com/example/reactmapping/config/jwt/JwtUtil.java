package com.example.reactmapping.config.jwt;

import com.example.reactmapping.norm.Token;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    @Value("${jwt.secretKey}")
    private String secretKey;

    private final String TokenType = "tokenType";
    private final String UserEmail = "userEmail";


    public String createToken(String userEmail, String tokenType){

        Claims claims= Jwts.claims();
        claims.put(TokenType, tokenType);
        claims.put(UserEmail,userEmail);
//        String key = tokenType.equals(Token.TokenType.ACCESS.name()) ? secretKey:secretKey;
        String key = secretKey;
        Long expiredTime = tokenType.equals(Token.TokenType.ACCESS.name()) ?Token.TokenTime.accessToken.getExpiredTime() :Token.TokenTime.refreshToken.getExpiredTime();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredTime))
                .signWith(SignatureAlgorithm.HS256,key)
                .compact();
    }

    public boolean isExpired(String token){
        try{
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getExpiration().before(new Date());
            return false;
        }catch (Exception e){
            return true;
        }
        //토큰이 지금 보다 이전에 expired 됐다면 만료된 것
    }
    public String getTokenType(String token){
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get(TokenType,String.class);
    }

    public String getUserEmail(String token){
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get(UserEmail,String.class);
    }

}
