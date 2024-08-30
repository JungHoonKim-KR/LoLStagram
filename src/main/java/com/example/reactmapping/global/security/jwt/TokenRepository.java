package com.example.reactmapping.global.security.jwt;

import com.example.reactmapping.global.norm.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Repository
public class TokenRepository {
    private final RedisTemplate redisTemplate;
    private final JwtUtil jwtUtil;

    public void save(String token, String type){

        long expiredTime = (Objects.equals(type, Token.TokenType.ACCESS.name()))? Token.TokenTime.accessToken.getExpiredTime() : Token.TokenTime.refreshToken.getExpiredTime();

        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set(jwtUtil.getUserEmail(token) +":" + type
                , token,
                expiredTime,TimeUnit.MILLISECONDS);
    }

    public void registerBlacklist(String token){

        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set(jwtUtil.getUserEmail(token) + ":blackList" ,token, jwtUtil.getTokenTime(token).getTime(),TimeUnit.MILLISECONDS);
    }

    public Boolean isBlacklisted(String token){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Object tokenObject = valueOperations.get(jwtUtil.getUserEmail(token) + ":blackList");
        return !Objects.isNull(tokenObject);
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
    }

}
