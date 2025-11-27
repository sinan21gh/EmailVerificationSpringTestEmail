package com.sinans.springsecurityauthenticationauthorization.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {

    private final EmailService emailService;

    @GetMapping("/email/{username}")
    public String sendTestEmail(@PathVariable String username) {
        emailService.sendWelcomeEmail(
                "sinanikleriya19@gmail.com",
                username
        );

        return "Email sent!";
    }
}

