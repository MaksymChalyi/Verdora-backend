package com.verdorabackend.controller;

import com.verdorabackend.dto.auth.AuthResult;
import com.verdorabackend.dto.request.SignInRequest;
import com.verdorabackend.dto.request.SignUpRequest;
import com.verdorabackend.dto.response.BaseResponse;
import com.verdorabackend.dto.response.BaseResponseFactory;
import com.verdorabackend.dto.response.SignInResponse;
import com.verdorabackend.dto.response.SignupResponse;
import com.verdorabackend.security.CookieService;
import com.verdorabackend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for authentication")
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final CookieService cookieService;

    @Operation(
            summary = "User registration",
            description = "Registers a new user and authenticates them by setting accessToken and refreshToken cookies"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User created and authenticated (cookies are set)",
                    headers = {
                            @Header(name = "Set-Cookie", description = "accessToken and refreshToken cookies")
                    },
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-04-21T13:55:49.772Z",
                                      "status": 201,
                                      "message": "User created",
                                      "data": {
                                        "email": "user@gmail.com"
                                      }
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Email already exists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-04-21T13:55:49.773Z",
                                      "status": 400,
                                      "message": "Email already exists",
                                      "data": null
                                    }
                                    """)
                    )
            )
    })
    @PostMapping("/register")
    public ResponseEntity<BaseResponse<SignupResponse>> signup(@Valid @RequestBody SignUpRequest request, HttpServletResponse response) {
        AuthResult result = authService.signup(request);
        cookieService.addAccessToken(response, result.accessToken());
        cookieService.addRefreshToken(response, result.refreshToken());
        return ResponseEntity.status(HttpStatus.CREATED).body(
                BaseResponseFactory.success(
                        HttpStatus.CREATED,
                        "User created",
                        new SignupResponse(result.email())
                )
        );
    }

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login successful",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-04-21T13:55:49.772Z",
                                      "status": 200,
                                      "message": "Login successful",
                                      "data": {
                                        "email": "user@gmail.com"
                                      }
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid email or password",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-04-21T13:55:49.773Z",
                                      "status": 401,
                                      "message": "Invalid email or password",
                                      "data": null
                                    }
                                    """)
                    )
            )
    })
    @Operation(
            summary = "User login",
            description = "Authenticates user and sets accessToken and refreshToken cookies"
    )
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<SignInResponse>> login(@RequestBody SignInRequest request, HttpServletResponse response) {
        AuthResult result = authService.login(request);
        cookieService.addAccessToken(response, result.accessToken());
        cookieService.addRefreshToken(response, result.refreshToken());
        return ResponseEntity.ok(
                BaseResponseFactory.success(
                        HttpStatus.OK,
                        "Login successful",
                        new SignInResponse(result.email())
                )
        );
    }

    @Operation(
            summary = "Refresh access token",
            description = "Uses refreshToken cookie to generate a new access token"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Token refreshed",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-04-21T13:55:49.772Z",
                                      "status": 200,
                                      "message": "Token refreshed",
                                      "data": null
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid or expired refresh token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-04-21T13:55:49.773Z",
                                      "status": 401,
                                      "message": "Invalid or expired refresh token",
                                      "data": null
                                    }
                                    """)
                    )
            )
    })
    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse<Void>> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookieService.extractRefreshToken(request);
        String newAccessToken = authService.refresh(refreshToken);
        cookieService.addAccessToken(response, newAccessToken);
        return ResponseEntity.ok(
                BaseResponseFactory.success(HttpStatus.OK, "Token refreshed")
        );
    }

    @Operation(
            summary = "Logout",
            description = "Clears accessToken and refreshToken cookies"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully logged out",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BaseResponse.class),
                    examples = @ExampleObject(value = """
                            {
                              "timestamp": "2026-04-21T13:55:49.772Z",
                              "status": 200,
                              "message": "Successfully logged out",
                              "data": null
                            }
                            """)
            )
    )
    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<Void>> logout(HttpServletResponse response) {
        cookieService.clearAuthCookies(response);
        return ResponseEntity.ok(
                BaseResponseFactory.success(HttpStatus.OK, "Successfully logged out")
        );
    }
}
