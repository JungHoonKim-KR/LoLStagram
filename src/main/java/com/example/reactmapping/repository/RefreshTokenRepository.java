package com.example.reactmapping.repository;

import com.example.reactmapping.norm.Token;
import com.example.reactmapping.object.RefreshToken;
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

    public void save(RefreshToken refreshToken){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set(refreshToken.getEmailId()+"refreshToken",refreshToken.getRefreshToken(), Token.INFO.getRefreshTokenTime(),TimeUnit.MILLISECONDS);
    }

    public Optional<RefreshToken> findById(final String emailId,String type) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Object refreshToken = valueOperations.get(emailId + type);
        if (Objects.isNull(refreshToken)) {
            return Optional.empty();
        }
        return Optional.of(new RefreshToken(emailId,refreshToken.toString()));
    }

    public void delete(String emailId, String type){
        redisTemplate.delete(emailId+type);
    }

}
