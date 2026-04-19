package com.verdorabackend.controller;

import com.verdorabackend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "Endpoints for managing users")
@RequestMapping("/users")
public class UserController {

    private final AuthService userService;

    @GetMapping("/current-user")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Get information about the current user")
    public ResponseEntity<?> getCurrentUser(Principal principal) {
        log.info("Request for current user details");
        return ResponseEntity.ok(principal);
    }




}
