package com.microservices.resilience.controller;

import com.microservices.resilience.dto.OrderRequest;
import com.microservices.resilience.dto.OrderResponse;
import com.microservices.resilience.service.RateLimiterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resilience/rate-limit")
@RequiredArgsConstructor
@Slf4j
public class RateLimiterController {

    private final RateLimiterService rateLimiterService;

    @PostMapping("/order")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        log.info("RateLimiter: Create order request received: {}", request.getOrderId());
        return ResponseEntity.ok(rateLimiterService.createOrderWithRateLimit(request));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String orderId) {
        log.info("RateLimiter: Get order request received: {}", orderId);
        return ResponseEntity.ok(rateLimiterService.getOrderWithRateLimit(orderId));
    }
}

