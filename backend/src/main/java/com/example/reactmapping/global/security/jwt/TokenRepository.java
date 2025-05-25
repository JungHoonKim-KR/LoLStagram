package com.example.reactmapping.global.security.jwt;

import com.example.reactmapping.global.norm.Token;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Repository
@Slf4j
public class TokenRepository {
    private final RedisTemplate redisTemplate;
    private final JwtUtil jwtUtil;

    public void save(String token, String type){

        long expiredTime = (Objects.equals(type, Token.TokenType.ACCESS.name()))? Token.TokenTime.accessToken.getExpiredTime() : Token.TokenTime.refreshToken.getExpiredTime();

        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set(jwtUtil.getUserEmail(token) +":" + type
                , token,
                expiredTime,TimeUnit.MILLISECONDS);
        log.info("{} 토큰 저장 완료", type);
    }

    public void registerBlacklist(String token){

        ValueOperations valueOperations = redisTemplate.opsForValue();
        if(jwtUtil.isExpired(token)){
            log.info("Token is already expired. No need to add to blacklist.");
            return;
        }
        Date expirationTime  = jwtUtil.getTokenTime(token);
        long expireTime = expirationTime.getTime();
        long now = System.currentTimeMillis();
        valueOperations.set(jwtUtil.getUserEmail(token) + ":blackList" ,token, expireTime - now ,TimeUnit.MILLISECONDS);
        log.info("블랙리스트 저장 완료");
    }

    public String findBlackList(String token){

        ValueOperations valueOperations = redisTemplate.opsForValue();
        Object tokenObject = valueOperations.get(jwtUtil.getUserEmail(token) + ":blackList");
        return tokenObject == null ? "" : tokenObject.toString();
    }

    public Optional<String> findToken(String token, String type) {

        ValueOperations valueOperations = redisTemplate.opsForValue();

        String emailId = jwtUtil.getUserEmail(token);

        Object tokenObject = valueOperations.get(emailId + ":" + type);
        if (Objects.isNull(tokenObject)) {
            return Optional.empty();
        }
        return Optional.of(tokenObject.toString());
    }

    public void delete(String refreshToken){
        redisTemplate.delete(jwtUtil.getUserEmail(refreshToken)+ ":" + Token.TokenType.REFRESH.name());
        log.info("토큰 삭제");
    }

}
