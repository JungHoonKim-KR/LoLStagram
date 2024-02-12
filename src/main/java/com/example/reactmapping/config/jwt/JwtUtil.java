package com.example.reactmapping.config.jwt;

import com.example.reactmapping.norm.Token;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    @Value("${jwt.accessKey}")
    private String accessKey;
    @Value("${jwt.refreshKey}")
    private String refreshKey;


    public String createToken(String userEmail, String tokenType){
        Claims claims= Jwts.claims();
        claims.put("tokenType", tokenType);
        claims.put("userEmail",userEmail);
        String key = tokenType.equals("ACCESS") ? accessKey:refreshKey;
        Long expiredTime = tokenType.equals("ACCESS") ? Token.INFO.getAccessTokenTime() :Token.INFO.getRefreshTokenTime();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredTime))
                .signWith(SignatureAlgorithm.HS256,key)
                .compact();
    }

    public boolean isExpired(String token,String tokenType){
        String key = tokenType.equals("ACCESS") ? accessKey:refreshKey;
        try{
            Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getExpiration().before(new Date());
            return false;
        }catch (Exception e){
            return true;
        }
        //토큰이 지금 보다 이전에 expired 됐다면 만료된 것
    }


    public String getTokenType(String token, String tokenType, HttpServletResponse response){
        String key = tokenType.equals("ACCESS") ? accessKey:refreshKey;
        try{
           return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().get("tokenType", String.class);
        }catch (Exception e) {
            // 예외 발생 시 클라이언트에게 에러 메시지 반환
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            // 에러 메시지 작성
            String jsonPayload = "{\"message\" : \"%s\"}";
            String errorMessage = String.format(jsonPayload, e.getMessage());

            try {
                OutputStream outputStream = response.getOutputStream();
                outputStream.write(errorMessage.getBytes());
            } catch (IOException | java.io.IOException ioException) {
                ioException.printStackTrace();
            }

            return null;
        }

    }
    public String getUserEmail(String token,String tokenType){
        String key = tokenType.equals("ACCESS") ? accessKey:refreshKey;
        return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().get("userEmail",String.class);
    }

}
