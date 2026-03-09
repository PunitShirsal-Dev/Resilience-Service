package com.microservices.resilience.service;

import com.microservices.resilience.client.OrderServiceClient;
import com.microservices.resilience.dto.OrderRequest;
import com.microservices.resilience.dto.OrderResponse;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class BulkheadService {

    private final OrderServiceClient orderServiceClient;

    @Bulkhead(name = "orderService", fallbackMethod = "createOrderFallback", type = Bulkhead.Type.SEMAPHORE)
    public OrderResponse createOrderWithBulkhead(OrderRequest request) {
        log.info("Creating order with Bulkhead pattern (Semaphore): {}", request.getOrderId());
        return orderServiceClient.createOrder(request);
    }

    @Bulkhead(name = "orderService", fallbackMethod = "getOrderAsyncFallback", type = Bulkhead.Type.THREADPOOL)
    public CompletableFuture<OrderResponse> getOrderWithThreadPoolBulkhead(String orderId) {
        log.info("Getting order with ThreadPool Bulkhead pattern: {}", orderId);
        return CompletableFuture.supplyAsync(() -> orderServiceClient.getOrder(orderId));
    }

    // Fallback for semaphore bulkhead
    private OrderResponse createOrderFallback(OrderRequest request, Exception e) {
        log.error("Bulkhead: Too many concurrent calls - {}", e.getMessage());
        return new OrderResponse(
            request.getOrderId(),
            "REJECTED",
            "Service is currently busy. Please try again in a moment.",
            request.getAmount(),
            System.currentTimeMillis()
        );
    }

    // Fallback for thread pool bulkhead
    private CompletableFuture<OrderResponse> getOrderAsyncFallback(String orderId, Exception e) {
        log.error("ThreadPool Bulkhead: Too many concurrent calls - {}", e.getMessage());
        return CompletableFuture.completedFuture(new OrderResponse(
            orderId,
            "REJECTED",
            "Service is currently busy. Please try again in a moment.",
            0.0,
            System.currentTimeMillis()
        ));
    }
}

