package com.mini.school.erp.gateway_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final ReactiveStringRedisTemplate redisTemplate;
    private static final String PREFIX = "auth:token:";

    public Mono<Boolean> validateTokenPresent(String username, String token) {
        return redisTemplate.opsForValue()
                .get(PREFIX + username)
                .map(stored -> stored != null && stored.equals(token))
                .switchIfEmpty(Mono.just(false));
    }

    public boolean isTokenValid(String username, String token) {
        return validateTokenPresent(username, token).blockOptional().orElse(false);
    }

}
