package com.verdorabackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Check Service availability", description = "Endpoints ping-pong")
@RequestMapping("/health")
public class HealthCheckController {

    @GetMapping("/ping")
    @Operation(summary = "Check service availability")
    public String ping() {
        return "pong";
    }

}
