package com.microservices.resilience.controller;

import com.microservices.resilience.dto.OrderRequest;
import com.microservices.resilience.dto.OrderResponse;
import com.microservices.resilience.service.CircuitBreakerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resilience/circuit-breaker")
@RequiredArgsConstructor
@Slf4j
public class CircuitBreakerController {

    private final CircuitBreakerService circuitBreakerService;

    @PostMapping("/order")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        log.info("Circuit Breaker: Create order request received: {}", request.getOrderId());
        OrderResponse response = circuitBreakerService.createOrderWithCircuitBreaker(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String orderId) {
        log.info("Circuit Breaker: Get order request received: {}", orderId);
        OrderResponse response = circuitBreakerService.getOrderWithCircuitBreaker(orderId);
        return ResponseEntity.ok(response);
    }
}

