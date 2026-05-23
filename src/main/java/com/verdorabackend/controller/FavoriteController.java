package com.verdorabackend.controller;

import com.verdorabackend.dto.response.BaseResponse;
import com.verdorabackend.dto.response.BaseResponseFactory;
import com.verdorabackend.dto.response.FavoriteResponse;
import com.verdorabackend.security.UserPrincipal;
import com.verdorabackend.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Favorites", description = "Endpoints for managing favorite products")
@RequestMapping("/favorites")
@SecurityRequirement(name = "Cookie-based Authentication")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Operation(summary = "Get favorites", description = "Returns all favorite products of the current user")
    @ApiResponse(responseCode = "200", description = "Favorites returned")
    @GetMapping
    public ResponseEntity<BaseResponse<List<FavoriteResponse>>> getFavorites(
            @AuthenticationPrincipal UserPrincipal principal) {
        log.info("Request to get favorites for userId={}", principal.getUser().getId());

        List<FavoriteResponse> response = favoriteService.getFavorites(principal.getUser().getId());

        return ResponseEntity.ok(
                BaseResponseFactory.success(HttpStatus.OK, "Favorites fetched successfully", response)
        );
    }

    @Operation(summary = "Add to favorites", description = "Adds a product to favorites")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Added to favorites"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "409", description = "Product already in favorites")
    })
    @PostMapping("/{productId}")
    public ResponseEntity<BaseResponse<FavoriteResponse>> addFavorite(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long productId) {
        log.info("Request to add productId={} to favorites for userId={}",
                productId, principal.getUser().getId());

        FavoriteResponse response = favoriteService.addFavorite(
                principal.getUser().getId(), productId);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                BaseResponseFactory.success(HttpStatus.CREATED, "Added to favorites", response)
        );
    }

    @Operation(summary = "Remove from favorites", description = "Removes a product from favorites")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Removed from favorites"),
            @ApiResponse(responseCode = "404", description = "Product not found in favorites")
    })
    @DeleteMapping("/{productId}")
    public ResponseEntity<BaseResponse<Void>> removeFavorite(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long productId) {
        log.info("Request to remove productId={} from favorites for userId={}",
                productId, principal.getUser().getId());

        favoriteService.removeFavorite(principal.getUser().getId(), productId);

        return ResponseEntity.ok(
                BaseResponseFactory.success(HttpStatus.OK, "Removed from favorites")
        );
    }
}
