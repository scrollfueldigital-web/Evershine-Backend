package com.evershine.EvershineServer.securityConf.service;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class RateLimitService {

    private final Cache<String, Counter> ipCache = newCache(30);
    private final Cache<String, Counter> emailRequestCache = newCache(30);
    private final Cache<String, Counter> hourlyEmailCache = newCache(120);
    private final Cache<String, Counter> dailyEmailCache = newCache(2880);

    private final Cache<String, Boolean> emailCooldownCache =
            Caffeine.newBuilder()
                    .expireAfterWrite(Duration.ofMinutes(3))
                    .maximumSize(1_000)
                    .build();

    private Cache<String, Counter> newCache(long idleMinutes) {
        return Caffeine.newBuilder()
                .expireAfterAccess(Duration.ofMinutes(idleMinutes))
                .maximumSize(1_000)
                .build();
    }

    public synchronized boolean allowForgotRequest(
            String ip, String email) {

        Instant now = Instant.now();

        boolean ipAllowed = incrementIfAllowed(
                ipCache, ip, 5, Duration.ofMinutes(15), now);

        boolean emailAllowed = incrementIfAllowed(
                emailRequestCache, email, 5,
                Duration.ofMinutes(15), now);

        return ipAllowed && emailAllowed;
    }

    public synchronized boolean allowEmailSend(String email) {
        if (emailCooldownCache.getIfPresent(email) != null) {
            return false;
        }

        Instant now = Instant.now();

        Counter hourly = getCounter(
                hourlyEmailCache, email, Duration.ofHours(1), now);

        Counter daily = getCounter(
                dailyEmailCache, "GLOBAL",
                Duration.ofHours(24), now);

        if (hourly.count >= 5 || daily.count >= 250) {
            return false;
        }

        hourly.count++;
        daily.count++;

        emailCooldownCache.put(email, true);
        return true;
    }

    private boolean incrementIfAllowed(
            Cache<String, Counter> cache,
            String key,
            int limit,
            Duration window,
            Instant now) {

        Counter counter = getCounter(cache, key, window, now);

        if (counter.count >= limit) {
            return false;
        }

        counter.count++;
        return true;
    }

    private Counter getCounter(
            Cache<String, Counter> cache,
            String key,
            Duration window,
            Instant now) {

        Counter counter = cache.get(key, ignored -> new Counter(now));

        if (!now.isBefore(counter.windowStart.plus(window))) {
            counter.count = 0;
            counter.windowStart = now;
        }

        return counter;
    }

    private static class Counter {
        private int count;
        private Instant windowStart;

        private Counter(Instant now) {
            this.count = 0;
            this.windowStart = now;
        }
    }
}