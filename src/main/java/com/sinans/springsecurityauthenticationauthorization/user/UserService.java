package com.sinans.springsecurityauthenticationauthorization.user;



import com.sinans.springsecurityauthenticationauthorization.jwt.JwtService;
import jakarta.transaction.Transactional;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepo repo;

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private JwtService jwt;
    @Autowired
    private VerificationTokenRepo verificationTokenRepo;

    @Autowired
    private EmailService emailService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public ResponseEntity<?> register(Users user) {

        if (repo.existsById(user.getUsername()))
            return new ResponseEntity<>(HttpStatus.CONFLICT);

        user.setEnabled(false);
        user.setPassword(encoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        repo.save(user);


        VerificationToken token = new VerificationToken(
                null,
                UUID.randomUUID().toString(),
                user,
                Instant.now().plus(1, ChronoUnit.HOURS)
        );

        verificationTokenRepo.save(token);

        emailService.sendVerificationEmail(user.getEmail(), user.getUsername(), token.getToken());

        return ResponseEntity.status(HttpStatus.CREATED).body("User registered. Verify email!");

    }

    @Transactional
    public ResponseEntity<String> verifyUser(String tokenValue) {

        VerificationToken token = verificationTokenRepo.findByToken(tokenValue)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (token.getExpiresAt().isBefore(Instant.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token expired");
        }

        Users user = token.getUser();
        user.setEnabled(true);
        verificationTokenRepo.delete(token); // remove used token

        return ResponseEntity.ok("Email verified successfully!");
    }


    public String login(LoginRequest users) {
        Users users1 = repo.findByUsername(users.getUsername());
        if (!users1.isEnabled()) {
            return "Verify Account";
        }
        Authentication auth = manager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        users.getUsername(),
                        users.getPassword()
                ));


        if (auth.isAuthenticated() && users1.isEnabled())
            return jwt.generateToken((UserDetails) auth.getPrincipal());
        return "Failed";
    }

    @PreAuthorize("hasAuthority('admin:delete')")
    public void deleteUser(String username) {
        repo.deleteById(username);
    }
    

}

