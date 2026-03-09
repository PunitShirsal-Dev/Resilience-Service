package com.microservices.resilience.service;

import com.microservices.resilience.client.OrderServiceClient;
import com.microservices.resilience.client.PaymentServiceClient;
import com.microservices.resilience.client.InventoryServiceClient;
import com.microservices.resilience.dto.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class CombinedResilienceService {

    private final OrderServiceClient orderServiceClient;
    private final PaymentServiceClient paymentServiceClient;
    private final InventoryServiceClient inventoryServiceClient;

    // Combined: Circuit Breaker + Retry + Rate Limiter
    @CircuitBreaker(name = "orderService", fallbackMethod = "placeOrderFallback")
    @Retry(name = "orderService")
    @RateLimiter(name = "orderService")
    public OrderResponse placeOrderWithAllPatterns(OrderRequest orderRequest) {
        log.info("Placing order with combined resilience patterns: {}", orderRequest.getOrderId());

        // Check inventory first
        InventoryResponse inventory = inventoryServiceClient.checkInventory(
            orderRequest.getProductId(),
            orderRequest.getQuantity()
        );

        if (!inventory.getInStock()) {
            return new OrderResponse(
                orderRequest.getOrderId(),
                "OUT_OF_STOCK",
                "Product is out of stock",
                orderRequest.getAmount(),
                System.currentTimeMillis()
            );
        }

        // Create order
        OrderResponse orderResponse = orderServiceClient.createOrder(orderRequest);

        // Process payment
        PaymentRequest paymentRequest = new PaymentRequest(
            "PAY-" + orderRequest.getOrderId(),
            orderRequest.getOrderId(),
            orderRequest.getAmount(),
            "CREDIT_CARD"
        );

        PaymentResponse paymentResponse = paymentServiceClient.processPayment(paymentRequest);

        if ("SUCCESS".equals(paymentResponse.getStatus())) {
            orderResponse.setStatus("COMPLETED");
            orderResponse.setMessage("Order placed and payment processed successfully");
        } else {
            orderResponse.setStatus("PAYMENT_FAILED");
            orderResponse.setMessage("Order created but payment failed");
        }

        return orderResponse;
    }

    // Combined: Circuit Breaker + Bulkhead + TimeLimiter
    @CircuitBreaker(name = "paymentService", fallbackMethod = "processPaymentFallback")
    @Bulkhead(name = "paymentService", type = Bulkhead.Type.SEMAPHORE)
    @TimeLimiter(name = "paymentService")
    public CompletableFuture<PaymentResponse> processPaymentAsync(PaymentRequest request) {
        log.info("Processing payment with combined patterns: {}", request.getPaymentId());
        return CompletableFuture.supplyAsync(() ->
            paymentServiceClient.processPayment(request)
        );
    }

    // Fallback for placeOrder
    private OrderResponse placeOrderFallback(OrderRequest request, Exception e) {
        log.error("Combined resilience: Fallback for placeOrder - {}", e.getMessage());
        return new OrderResponse(
            request.getOrderId(),
            "FALLBACK",
            "Unable to process order at this time. Please try again later.",
            request.getAmount(),
            System.currentTimeMillis()
        );
    }

    // Fallback for processPayment
    private CompletableFuture<PaymentResponse> processPaymentFallback(PaymentRequest request, Exception e) {
        log.error("Combined resilience: Fallback for processPayment - {}", e.getMessage());
        return CompletableFuture.completedFuture(new PaymentResponse(
            request.getPaymentId(),
            "FALLBACK",
            "Payment service is temporarily unavailable",
            request.getAmount()
        ));
    }
}

