package com.evershine.EvershineServer.securityConf.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
public class ResetPasswordTokenService {

    private final Cache<String, String> tokenCache = newCache();
    private final Cache<String, String> usernameCache = newCache();

    private Cache<String, String> newCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(15))
                .maximumSize(1_000)
                .build();
    }

    public synchronized String createToken(String username) {
        String oldToken = usernameCache.getIfPresent(username);

        if (oldToken != null) {
            tokenCache.invalidate(oldToken);
        }

        String token = UUID.randomUUID().toString();

        tokenCache.put(token, username);
        usernameCache.put(username, token);

        return token;
    }

    public synchronized Optional<String> consumeToken(String token) {
        String username = tokenCache.getIfPresent(token);

        if (username == null) {
            return Optional.empty();
        }

        tokenCache.invalidate(token);

        if (token.equals(usernameCache.getIfPresent(username))) {
            usernameCache.invalidate(username);
        }

        return Optional.of(username);
    }

    public synchronized boolean hasValidToken(String username) {
        String token = usernameCache.getIfPresent(username);

        return token != null
                && token.equals(tokenCache.getIfPresent(token));
    }
}
