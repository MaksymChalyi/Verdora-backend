package com.verdorabackend.controller;

import com.verdorabackend.dto.request.UpdateUserRequest;
import com.verdorabackend.dto.response.BaseResponse;
import com.verdorabackend.dto.response.BaseResponseFactory;
import com.verdorabackend.dto.response.UserResponse;
import com.verdorabackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "Endpoints for managing users")
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @SecurityRequirement(name = "Cookie-based Authentication")
    @Operation(
            summary = "Get current user",
            description = "Returns current authenticated user based on accessToken cookie"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User returned",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-05-09T13:55:49.772Z",
                                      "status": 200,
                                      "message": "User fetched successfully",
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
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-05-09T13:55:49.773Z",
                                      "status": 401,
                                      "message": "Unauthorized",
                                      "data": null
                                    }
                                    """)
                    )
            )
    })
    @GetMapping("/current-user")
    public ResponseEntity<BaseResponse<UserResponse>> getCurrentUser(Principal principal) {
        log.info("Request for current user details");
        UserResponse response = userService.getUserByEmail(principal.getName());
        return ResponseEntity.ok(
                BaseResponseFactory.success(
                        HttpStatus.OK,
                        "User fetched successfully",
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
            summary = "Delete user account",
            description = "Deletes user account by ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User deleted successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-05-09T13:55:49.772Z",
                                      "status": 200,
                                      "message": "User deleted successfully",
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
    @DeleteMapping("/{userId}")
    public ResponseEntity<BaseResponse<Void>> deleteUser(@PathVariable Long userId) {
        log.info("Request to delete user: userId={}", userId);

        userService.deleteUser(userId);

        return ResponseEntity.ok(
                BaseResponseFactory.success(
                        HttpStatus.OK,
                        "User deleted successfully",
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
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
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
