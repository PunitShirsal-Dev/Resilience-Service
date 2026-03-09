package com.microservices.resilience.service;

import com.microservices.resilience.client.InventoryServiceClient;
import com.microservices.resilience.dto.InventoryResponse;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class TimeoutService {

    private final InventoryServiceClient inventoryServiceClient;

    @TimeLimiter(name = "inventoryService", fallbackMethod = "checkInventoryFallback")
    public CompletableFuture<InventoryResponse> checkInventoryWithTimeout(String productId, Integer quantity) {
        log.info("Checking inventory with Timeout pattern: product={}, quantity={}", productId, quantity);
        return CompletableFuture.supplyAsync(() ->
            inventoryServiceClient.checkInventory(productId, quantity)
        );
    }

    // Fallback method for timeout
    private CompletableFuture<InventoryResponse> checkInventoryFallback(String productId, Integer quantity, Exception e) {
        log.error("Timeout: Fallback for checkInventory - {}", e.getMessage());
        return CompletableFuture.completedFuture(new InventoryResponse(
            productId,
            0,
            false,
            "Inventory check timed out. Please try again later."
        ));
    }
}

