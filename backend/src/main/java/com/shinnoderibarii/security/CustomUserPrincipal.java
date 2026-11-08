package com.shinnoderibarii.security;

import lombok.Getter;

@Getter
public class CustomUserPrincipal {

    private final Long userId;
    private final String email;

    public CustomUserPrincipal(Long userId, String email) {
        this.userId = userId;
        this.email = email;
    }
}