package com.sinans.springsecurityauthenticationauthorization.user;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class VerificationTokenCleanupService {

    private final VerificationTokenRepo repo;

    @Scheduled(cron = "0 0 */6 * * *")
    public void cleanExpiredTokens() {
        repo.deleteByExpiresAtBefore(Instant.now());
    }
}

