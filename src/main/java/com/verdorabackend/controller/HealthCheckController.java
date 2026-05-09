package com.verdorabackend.controller;

import com.verdorabackend.dto.response.BaseResponse;
import com.verdorabackend.dto.response.BaseResponseFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Health", description = "Service availability endpoints")
@RequestMapping("/health")
public class HealthCheckController {

    @Operation(summary = "Check service availability (ping)")
    @ApiResponse(
            responseCode = "200",
            description = "Service is alive",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BaseResponse.class),
                    examples = @ExampleObject(value = """
                            {
                              "timestamp": "2026-04-21T13:55:49.772Z",
                              "status": 200,
                              "message": "Service is alive",
                              "data": "pong"
                            }
                            """)
            )
    )
    @GetMapping("/ping")
    public ResponseEntity<BaseResponse<String>> ping() {
        return ResponseEntity.ok(
                BaseResponseFactory.success(
                        HttpStatus.OK,
                        "Service is alive",
                        "pong"
                )
        );
    }
}
