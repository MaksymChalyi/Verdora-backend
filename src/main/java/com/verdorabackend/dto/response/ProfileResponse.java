package com.verdorabackend.dto.response;

public record ProfileResponse(
        String name,
        String email,
        String phone
) {
}