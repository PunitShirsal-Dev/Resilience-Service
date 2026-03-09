package com.microservices.resilience.service;

import com.microservices.resilience.client.OrderServiceClient;
import com.microservices.resilience.dto.OrderRequest;
import com.microservices.resilience.dto.OrderResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class CircuitBreakerService {

    private final OrderServiceClient orderServiceClient;

    @CircuitBreaker(name = "orderService", fallbackMethod = "createOrderFallback")
    public OrderResponse createOrderWithCircuitBreaker(OrderRequest request) {
        log.info("Creating order with Circuit Breaker pattern: {}", request.getOrderId());
        return orderServiceClient.createOrder(request);
    }

    @CircuitBreaker(name = "orderService", fallbackMethod = "getOrderFallback")
    public OrderResponse getOrderWithCircuitBreaker(String orderId) {
        log.info("Getting order with Circuit Breaker pattern: {}", orderId);
        return orderServiceClient.getOrder(orderId);
    }

    // Fallback method for createOrder
    private OrderResponse createOrderFallback(OrderRequest request, Exception e) {
        log.error("Circuit Breaker: Fallback for createOrder - {}", e.getMessage());
        return new OrderResponse(
            request.getOrderId(),
            "FALLBACK",
            "Order service is temporarily unavailable. Your order will be processed later.",
            request.getAmount(),
            System.currentTimeMillis()
        );
    }

    // Fallback method for getOrder
    private OrderResponse getOrderFallback(String orderId, Exception e) {
        log.error("Circuit Breaker: Fallback for getOrder - {}", e.getMessage());
        return new OrderResponse(
            orderId,
            "FALLBACK",
            "Unable to retrieve order details at this moment. Please try again later.",
            0.0,
            System.currentTimeMillis()
        );
    }
}

