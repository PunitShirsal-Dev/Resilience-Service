package com.microservices.resilience.controller;

import com.microservices.resilience.dto.OrderRequest;
import com.microservices.resilience.dto.OrderResponse;
import com.microservices.resilience.dto.PaymentRequest;
import com.microservices.resilience.dto.PaymentResponse;
import com.microservices.resilience.service.CombinedResilienceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/resilience/demo")
@RequiredArgsConstructor
@Slf4j
public class ResilienceDemoController {

    private final CombinedResilienceService combinedResilienceService;

    @PostMapping("/order")
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest request) {
        log.info("Resilience Demo: Place order request received: {}", request.getOrderId());
        return ResponseEntity.ok(combinedResilienceService.placeOrderWithAllPatterns(request));
    }

    @PostMapping("/payment")
    public ResponseEntity<PaymentResponse> processPayment(@RequestBody PaymentRequest request)
            throws ExecutionException, InterruptedException {
        log.info("Resilience Demo: Process payment request received: {}", request.getPaymentId());
        return ResponseEntity.ok(combinedResilienceService.processPaymentAsync(request).get());
    }

    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        return ResponseEntity.ok("Resilience Service is running");
    }
}

