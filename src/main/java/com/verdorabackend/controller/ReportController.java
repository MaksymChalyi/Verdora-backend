package com.verdorabackend.controller;

import com.verdorabackend.dto.response.*;
import com.verdorabackend.exception.InvalidReportParameterException;
import com.verdorabackend.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Report Management", description = "Endpoints for admin reports")
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Cookie-based Authentication")
    @Operation(
            summary = "Get top purchased products",
            description = "Returns up to 10 products with the highest total purchased quantity"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/top-purchased")
    public ResponseEntity<BaseResponse<List<TopPurchasedProductResponse>>> getTopPurchasedProducts() {
        log.info("Admin request to get top purchased products report");

        List<TopPurchasedProductResponse> response =
                reportService.getTopPurchasedProducts();

        return ResponseEntity.ok(
                BaseResponseFactory.success(
                        HttpStatus.OK,
                        "Top purchased products fetched successfully",
                        response
                )
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Cookie-based Authentication")
    @Operation(
            summary = "Get top cancelled products",
            description = "Returns up to 10 products with the highest total cancelled quantity"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/top-cancelled")
    public ResponseEntity<BaseResponse<List<TopCancelledProductResponse>>> getTopCancelledProducts() {
        log.info("Admin request to get top cancelled products report");

        List<TopCancelledProductResponse> response =
                reportService.getTopCancelledProducts();

        return ResponseEntity.ok(
                BaseResponseFactory.success(
                        HttpStatus.OK,
                        "Top cancelled products fetched successfully",
                        response
                )
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Cookie-based Authentication")
    @Operation(
            summary = "Get pending payment orders",
            description = "Returns orders that have remained in PENDING status longer than N days"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report returned"),
            @ApiResponse(responseCode = "400", description = "Invalid n parameter"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/pending-payment")
    public ResponseEntity<BaseResponse<List<PendingPaymentOrderResponse>>> getPendingPaymentOrders(
            @Parameter(
                    description = "Minimum number of days in pending payment status",
                    example = "7",
                    schema = @Schema(type = "integer", minimum = "1")
            )
            @RequestParam(required = false) String n) {

        int days = parsePositiveDays(n);

        log.info("Admin request to get orders pending payment longer than {} days", days);

        List<PendingPaymentOrderResponse> response =
                reportService.getPendingPaymentOrders(days);

        return ResponseEntity.ok(
                BaseResponseFactory.success(
                        HttpStatus.OK,
                        "Pending payment orders fetched successfully",
                        response
                )
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Cookie-based Authentication")
    @Operation(
            summary = "Get revenue report",
            description = "Returns revenue grouped by hour, day, week or month"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Revenue report returned"),
            @ApiResponse(responseCode = "400", description = "Invalid report parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/revenue")
    public ResponseEntity<BaseResponse<List<RevenueReportResponse>>> getRevenueReport(
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(required = false) String groupBy
    ) {
        LocalDate from = parseDate(dateFrom, "dateFrom");
        LocalDate to = parseDate(dateTo, "dateTo");
        String normalizedGroupBy = parseGroupBy(groupBy);

        if (from.isAfter(to)) {
            throw new InvalidReportParameterException(
                    "dateFrom must not be later than dateTo"
            );
        }

        log.info(
                "Admin request to get revenue report from {} to {}, groupBy={}",
                from,
                to,
                normalizedGroupBy
        );

        List<RevenueReportResponse> response =
                reportService.getRevenueReport(
                        from,
                        to,
                        normalizedGroupBy
                );

        return ResponseEntity.ok(
                BaseResponseFactory.success(
                        HttpStatus.OK,
                        "Revenue report fetched successfully",
                        response
                )
        );
    }

    private LocalDate parseDate(String value, String parameterName) {
        if (value == null || value.isBlank()) {
            throw new InvalidReportParameterException(
                    "Parameter '" + parameterName + "' is required"
            );
        }

        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException exception) {
            throw new InvalidReportParameterException(
                    "Parameter '" + parameterName + "' must use yyyy-MM-dd format"
            );
        }
    }

    private String parseGroupBy(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidReportParameterException(
                    "Parameter 'groupBy' is required"
            );
        }

        String normalized = value.toUpperCase();

        if (!Set.of("HOUR", "DAY", "WEEK", "MONTH").contains(normalized)) {
            throw new InvalidReportParameterException(
                    "Parameter 'groupBy' must be one of: HOUR, DAY, WEEK, MONTH"
            );
        }

        return normalized;
    }

    private int parsePositiveDays(String n) {
        if (n == null || n.isBlank()) {
            throw new InvalidReportParameterException();
        }

        try {
            int days = Integer.parseInt(n);

            if (days < 1) {
                throw new InvalidReportParameterException();
            }

            return days;
        } catch (NumberFormatException exception) {
            throw new InvalidReportParameterException();
        }
    }

}
