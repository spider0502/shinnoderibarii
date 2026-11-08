
package com.shinnoderibarii.controller;

import com.shinnoderibarii.dto.LoginRequest;
import com.shinnoderibarii.dto.LoginResponse;
import com.shinnoderibarii.entity.User;
import com.shinnoderibarii.security.CustomUserPrincipal;
import com.shinnoderibarii.service.JwtService;
import com.shinnoderibarii.service.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import java.util.Map;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(
            UserService userService,
            JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request) {

        User user = userService.login(request);

        String token = jwtService.generateToken(user.getEmail());

        return new LoginResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                token,
                "Bearer");
    }

    @GetMapping("/me")
    public Map<String, String> currentUser(
            Authentication authentication) {

        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

        // Long userId = principal.getUserId();
        String email = principal.getEmail();
        return Map.of(
                "email", email);
    }
}
