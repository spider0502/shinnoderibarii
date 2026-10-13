
package com.shinnoderibarii.dto;

public record UserRegisterResponse(
        Long id,
        String name,
        String email,
        String role) {
}
