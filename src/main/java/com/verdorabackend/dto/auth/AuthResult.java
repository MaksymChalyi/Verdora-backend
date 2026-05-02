package com.verdorabackend.dto.auth;

public record AuthResult(
        String email,
        String accessToken,
        String refreshToken) {
}
