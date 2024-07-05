package com.example.reactmapping.repository;

import com.example.reactmapping.config.jwt.JwtUtil;
import com.example.reactmapping.norm.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Repository
public class RefreshTokenRepository {
    private final RedisTemplate redisTemplate;
    private final JwtUtil jwtUtil;


    public void save(String token){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set(jwtUtil.getUserEmail(token) +":" + Token.TokenType.REFRESH.name()
                , token,
                Token.TokenTime.refreshToken.getExpiredTime(),TimeUnit.MILLISECONDS);
    }

    public Optional<String> findByRefreshToken(String refreshToken) {
        String emailId = jwtUtil.getUserEmail(refreshToken);
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Object refreshTokenObject = valueOperations.get(emailId + ":" + Token.TokenType.REFRESH.name());
        if (Objects.isNull(refreshTokenObject)) {
            return Optional.empty();
        }
        return Optional.of(jwtUtil.createToken(emailId,Token.TokenType.REFRESH.name()));
    }

    public void delete(String refreshToken){
        redisTemplate.delete(jwtUtil.getUserEmail(refreshToken)+ ":" + Token.TokenType.REFRESH.name());
    }

}
