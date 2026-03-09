package com.microservices.resilience.controller;

import com.microservices.resilience.dto.OrderRequest;
import com.microservices.resilience.dto.OrderResponse;
import com.microservices.resilience.service.BulkheadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/resilience/bulkhead")
@RequiredArgsConstructor
@Slf4j
public class BulkheadController {

    private final BulkheadService bulkheadService;

    @PostMapping("/order")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        log.info("Bulkhead: Create order request received: {}", request.getOrderId());
        return ResponseEntity.ok(bulkheadService.createOrderWithBulkhead(request));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String orderId) throws ExecutionException, InterruptedException {
        log.info("Bulkhead: Get order request received: {}", orderId);
        return ResponseEntity.ok(bulkheadService.getOrderWithThreadPoolBulkhead(orderId).get());
    }
}

