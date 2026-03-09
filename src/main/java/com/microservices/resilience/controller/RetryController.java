package com.microservices.resilience.controller;

import com.microservices.resilience.dto.PaymentRequest;
import com.microservices.resilience.dto.PaymentResponse;
import com.microservices.resilience.service.RetryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resilience/retry")
@RequiredArgsConstructor
@Slf4j
public class RetryController {

    private final RetryService retryService;

    @PostMapping("/payment")
    public ResponseEntity<PaymentResponse> processPayment(@RequestBody PaymentRequest request) {
        log.info("Retry: Process payment request received: {}", request.getPaymentId());
        return ResponseEntity.ok(retryService.processPaymentWithRetry(request));
    }

    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable String paymentId) {
        log.info("Retry: Get payment request received: {}", paymentId);
        return ResponseEntity.ok(retryService.getPaymentWithRetry(paymentId));
    }
}

