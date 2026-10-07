package com.verdorabackend.controller;

import com.verdorabackend.dto.response.BaseResponse;
import com.verdorabackend.dto.response.BaseResponseFactory;
import com.verdorabackend.dto.response.FavoriteResponse;
import com.verdorabackend.dto.response.FavoriteStateResponse;
import com.verdorabackend.security.UserPrincipal;
import com.verdorabackend.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Favorites", description = "Endpoints for managing favorite products")
@RequestMapping("/favorites")
@SecurityRequirement(name = "Cookie-based Authentication")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Operation(
            summary = "Get favorites",
            description = "Returns paginated favorites. Order: most recently added first, "
                    + "then lowest productId. Page starts at 0; default/max size is 12."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Page of favorites or empty page"),
            @ApiResponse(responseCode = "400", description = "Invalid page or size"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<BaseResponse<Page<FavoriteResponse>>> getFavorites(
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault(size = 12,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {
        Long userId = principal.getUser().getId();
        Pageable limitedPageable = PageRequest.of(pageable.getPageNumber(), Math.min(pageable.getPageSize(), 12), pageable.getSort());
        Page<FavoriteResponse> response =                favoriteService.getFavorites(userId, limitedPageable);

        return ResponseEntity.ok(
                BaseResponseFactory.success(
                        HttpStatus.OK,
                        "Favorites fetched successfully",
                        response
                )
        );
    }

    @Operation(summary = "Check if product is in favorites", description = "Returns true/false")
    @ApiResponse(responseCode = "200", description = "Check result returned")
    @GetMapping("/{productId}")
    public ResponseEntity<BaseResponse<Boolean>> isFavorite(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long productId) {
        log.info("Request to check favorite productId={} for userId={}", productId, principal.getUser().getId());

        boolean result = favoriteService.isFavorite(principal.getUser().getId(), productId);

        return ResponseEntity.ok(
                BaseResponseFactory.success(HttpStatus.OK, "Favorite status fetched", result)
        );
    }

    @Operation(
            summary = "Add to favorites",
            description = "Adds a product to favorites and returns its updated favorite state"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Product added to favorites"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Product already in favorites"
            )
    })
    @PostMapping("/{productId}")
    public ResponseEntity<BaseResponse<FavoriteStateResponse>> addFavorite(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long productId) {
        log.info("Request to add productId={} to favorites for userId={}", productId, principal.getUser().getId());
        FavoriteStateResponse response = favoriteService.addFavorite(principal.getUser().getId(), productId);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                BaseResponseFactory.success(HttpStatus.CREATED, "Added to favorites", response)
        );
    }

    @Operation(
            summary = "Remove from favorites",
            description = "Removes a product from favorites and returns its updated favorite state"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Product removed from favorites"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found in favorites"
            )
    })
    @DeleteMapping("/{productId}")
    public ResponseEntity<BaseResponse<FavoriteStateResponse>> removeFavorite(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long productId) {
        log.info("Request to remove productId={} from favorites for userId={}", productId, principal.getUser().getId());
        FavoriteStateResponse response = favoriteService.removeFavorite(principal.getUser().getId(), productId);
        return ResponseEntity.ok(
                BaseResponseFactory.success(HttpStatus.OK, "Removed from favorites", response)
        );
    }

}
