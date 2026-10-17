package com.verdorabackend.dto.response;

import com.verdorabackend.entity.Role;

public record ProfileResponse(
        String name,
        String email,
        String phone,
        Role role
) {
}
