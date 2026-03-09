package com.microservices.resilience.service;

import com.microservices.resilience.client.OrderServiceClient;
import com.microservices.resilience.dto.OrderRequest;
import com.microservices.resilience.dto.OrderResponse;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RateLimiterService {

    private final OrderServiceClient orderServiceClient;

    @RateLimiter(name = "orderService", fallbackMethod = "createOrderFallback")
    public OrderResponse createOrderWithRateLimit(OrderRequest request) {
        log.info("Creating order with Rate Limiter pattern: {}", request.getOrderId());
        return orderServiceClient.createOrder(request);
    }

    @RateLimiter(name = "inventoryService", fallbackMethod = "getOrderFallback")
    public OrderResponse getOrderWithRateLimit(String orderId) {
        log.info("Getting order with Rate Limiter pattern: {}", orderId);
        return orderServiceClient.getOrder(orderId);
    }

    // Fallback for rate limiter
    private OrderResponse createOrderFallback(OrderRequest request, Exception e) {
        log.error("Rate Limiter: Too many requests - {}", e.getMessage());
        return new OrderResponse(
            request.getOrderId(),
            "RATE_LIMITED",
            "Too many requests. Please slow down and try again.",
            request.getAmount(),
            System.currentTimeMillis()
        );
    }

    private OrderResponse getOrderFallback(String orderId, Exception e) {
        log.error("Rate Limiter: Too many requests - {}", e.getMessage());
        return new OrderResponse(
            orderId,
            "RATE_LIMITED",
            "Too many requests. Please slow down and try again.",
            0.0,
            System.currentTimeMillis()
        );
    }
}

