package com.verdorabackend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Schema(description = "Revenue report item")
public record RevenueReportResponse(
        @Schema(description = "Start of the aggregation period")
        OffsetDateTime periodStart,

        @Schema(description = "Revenue for the period", example = "12500.50")
        BigDecimal revenue
) {
}
