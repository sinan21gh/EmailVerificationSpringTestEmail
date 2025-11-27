package com.sinans.springsecurityauthenticationauthorization.user;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.time.Instant;
import java.util.Optional;

public interface VerificationTokenRepo
        extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);

    @Modifying
    @Transactional
    void deleteByExpiresAtBefore(Instant now);
}

