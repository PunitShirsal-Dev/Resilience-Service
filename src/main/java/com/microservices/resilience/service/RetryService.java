package com.microservices.resilience.service;

import com.microservices.resilience.client.PaymentServiceClient;
import com.microservices.resilience.dto.PaymentRequest;
import com.microservices.resilience.dto.PaymentResponse;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RetryService {

    private final PaymentServiceClient paymentServiceClient;

    @Retry(name = "paymentService", fallbackMethod = "processPaymentFallback")
    public PaymentResponse processPaymentWithRetry(PaymentRequest request) {
        log.info("Processing payment with Retry pattern (with exponential backoff): {}", request.getPaymentId());
        return paymentServiceClient.processPayment(request);
    }

    @Retry(name = "paymentService", fallbackMethod = "getPaymentFallback")
    public PaymentResponse getPaymentWithRetry(String paymentId) {
        log.info("Getting payment with Retry pattern: {}", paymentId);
        return paymentServiceClient.getPayment(paymentId);
    }

    // Fallback method for processPayment
    private PaymentResponse processPaymentFallback(PaymentRequest request, Exception e) {
        log.error("Retry exhausted: Fallback for processPayment - {}", e.getMessage());
        return new PaymentResponse(
            request.getPaymentId(),
            "FAILED",
            "Payment processing failed after multiple retries. Please try again later.",
            request.getAmount()
        );
    }

    // Fallback method for getPayment
    private PaymentResponse getPaymentFallback(String paymentId, Exception e) {
        log.error("Retry exhausted: Fallback for getPayment - {}", e.getMessage());
        return new PaymentResponse(
            paymentId,
            "UNKNOWN",
            "Unable to retrieve payment status after multiple retries.",
            0.0
        );
    }
}

