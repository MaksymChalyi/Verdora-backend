package com.verdorabackend.controller;

import com.verdorabackend.dto.request.SignInRequest;
import com.verdorabackend.dto.request.SignUpRequest;
import com.verdorabackend.dto.response.general.ApiResponse;
import com.verdorabackend.dto.response.general.ApiResponseFactory;
import com.verdorabackend.dto.response.SignInResponse;
import com.verdorabackend.dto.response.SignupResponse;
import com.verdorabackend.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Endpoints for authentication")
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @RequestBody SignUpRequest request) {
        SignupResponse response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseFactory.success(
                        HttpStatus.CREATED,
                        "User created",
                        response
                )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<SignInResponse>> login(@RequestBody SignInRequest request) {
        SignInResponse response = authService.login(request);
        return ResponseEntity.ok(
                ApiResponseFactory.success(
                        HttpStatus.OK,
                        "Login successful",
                        response
                )
        );
    }

}
