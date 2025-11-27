package com.sinans.springsecurityauthenticationauthorization.user;


import com.sinans.springsecurityauthenticationauthorization.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private UserRepo repo;

    @Autowired
    private JwtService jwt;

    @Autowired
    private VerificationTokenRepo repository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Users users) {
        return service.register(users);
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verify(@RequestParam String token) {
        return service.verifyUser(token);
    }


    @PostMapping("/login")
    public String login(@RequestBody LoginRequest users) {
        return service.login(users);
    }

    @GetMapping("/me")
    public ResponseEntity<?> profile(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer "))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String token = header.substring(7);
        String username = jwt.extractUsername(token);

        return ResponseEntity.ok(repo.findByUsername(username));
    }

    @PutMapping("/admin/role/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUserRole(
            @PathVariable String username,
            @RequestParam Role role) {

        Users user = repo.findByUsername(username);
        if (user == null) return ResponseEntity.notFound().build();

        user.setRole(role);
        repo.save(user);

        return ResponseEntity.ok("User role updated to " + role);
    }

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Users> getAllUsers() {
        return repo.findAll();
    }

    @DeleteMapping("/admin/delete/{username}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {

        Users user = repo.findByUsername(username);
        if (user == null) return ResponseEntity.notFound().build();

        service.deleteUser(username);
        return ResponseEntity.ok("User deleted");
    }




}

