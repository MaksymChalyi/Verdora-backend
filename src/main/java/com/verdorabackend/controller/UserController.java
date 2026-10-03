package com.verdorabackend.controller;

import com.verdorabackend.dto.request.DeleteAccountRequest;
import com.verdorabackend.dto.request.UpdateUserRequest;
import com.verdorabackend.dto.response.BaseResponse;
import com.verdorabackend.dto.response.BaseResponseFactory;
import com.verdorabackend.dto.response.ProfileResponse;
import com.verdorabackend.dto.response.UserResponse;
import com.verdorabackend.security.CookieService;
import com.verdorabackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "Endpoints for managing users")
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final CookieService cookieService;

    @SecurityRequirement(name = "Cookie-based Authentication")
    @Operation(
            summary = "Get current user profile",
            description = "Returns profile data of the currently authenticated user"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Profile returned successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-10-03T13:55:49.772Z",
                                      "status": 200,
                                      "message": "Profile fetched successfully",
                                      "data": {
                                        "name": "Stepan",
                                        "email": "stepan@gmail.com",
                                        "phone": "+380989703417"
                                      }
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-10-03T13:55:49.773Z",
                                      "status": 401,
                                      "message": "Unauthorized",
                                      "data": null
                                    }
                                    """)
                    )
            )
    })
    @GetMapping("/current-user")
    public ResponseEntity<BaseResponse<ProfileResponse>> getCurrentUser(Principal principal) {
        log.info("Request for current user profile");
        UserResponse user = userService.getUserByEmail(principal.getName());
        ProfileResponse response = new ProfileResponse(
                user.name(),
                user.email(),
                user.phone()
        );

        return ResponseEntity.ok(
                BaseResponseFactory.success(
                        HttpStatus.OK,
                        "Profile fetched successfully",
                        response
                )
        );
    }

    @SecurityRequirement(name = "Cookie-based Authentication")
    @Operation(
            summary = "Update user profile",
            description = "Updates name and phone number of the user by ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-05-09T13:55:49.772Z",
                                      "status": 200,
                                      "message": "User updated successfully",
                                      "data": {
                                        "id": 1,
                                        "name": "Stepan",
                                        "email": "stepan@gmail.com",
                                        "phone": "+380989703417"
                                      }
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-05-09T13:55:49.773Z",
                                      "status": 400,
                                      "message": "Validation failed",
                                      "data": null
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-05-09T13:55:49.773Z",
                                      "status": 404,
                                      "message": "User not found",
                                      "data": null
                                    }
                                    """)
                    )
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        log.info("Request to update user: userId={}", id);
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(
                BaseResponseFactory.success(
                        HttpStatus.OK,
                        "User updated successfully",
                        response
                )
        );
    }

    @SecurityRequirement(name = "Cookie-based Authentication")
    @Operation(
            summary = "Delete current user account",
            description = "Deletes the currently authenticated user account after password confirmation"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Account deleted successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-10-03T13:55:49.772Z",
                                      "status": 200,
                                      "message": "Account deleted successfully",
                                      "data": null
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Password is missing or invalid request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized or invalid password",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-10-03T13:55:49.773Z",
                                      "status": 401,
                                      "message": "Invalid password",
                                      "data": null
                                    }
                                    """)
                    )
            )
    })
    @DeleteMapping("/current-user")
    public ResponseEntity<BaseResponse<Void>> deleteCurrentUser(
            @Valid @RequestBody DeleteAccountRequest request,
            Principal principal, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        log.info("Request to delete current user account");
        userService.deleteCurrentUser(
                principal.getName(),
                request.password()
        );

        cookieService.clearAuthCookies(httpServletResponse);

        HttpSession session = httpServletRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok(
                BaseResponseFactory.success(
                        HttpStatus.OK,
                        "Account deleted successfully",
                        null
                )
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Cookie-based Authentication")
    @Operation(
            summary = "Get all users",
            description = "Returns paginated list of all users. Admin only."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Users returned",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-05-09T13:55:49.772Z",
                                      "status": 200,
                                      "message": "Users fetched successfully",
                                      "data": {
                                        "content": [
                                          {
                                            "id": 1,
                                            "name": "Stepan",
                                            "email": "stepan@gmail.com",
                                            "phone": "+380989703417"
                                          }
                                        ],
                                        "totalElements": 1,
                                        "totalPages": 1,
                                        "size": 20,
                                        "number": 0
                                      }
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-05-09T13:55:49.773Z",
                                      "status": 403,
                                      "message": "Forbidden",
                                      "data": null
                                    }
                                    """)
                    )
            )
    })
    @GetMapping
    public ResponseEntity<BaseResponse<Page<UserResponse>>> getAllUsers(
            @PageableDefault(size = 12, sort = "id") Pageable pageable) {
        log.info("Request to get all users, page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<UserResponse> users = userService.getAllUsers(pageable);

        return ResponseEntity.ok(
                BaseResponseFactory.success(
                        HttpStatus.OK,
                        "Users fetched successfully",
                        users
                )
        );
    }

}
