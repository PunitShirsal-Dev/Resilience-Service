package com.microservices.resilience.client;

import com.microservices.resilience.dto.InventoryResponse;
import com.microservices.resilience.exception.ServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceClient {

    private final WebClient webClient;

    @Value("${external.services.inventory.url}")
    private String inventoryServiceUrl;

    public InventoryResponse checkInventory(String productId, Integer quantity) {
        log.info("Calling Inventory Service to check product: {} quantity: {}", productId, quantity);

        try {
            return webClient.get()
                    .uri(inventoryServiceUrl + "/" + productId + "?quantity=" + quantity)
                    .retrieve()
                    .bodyToMono(InventoryResponse.class)
                    .timeout(Duration.ofSeconds(3))
                    .block();
        } catch (Exception e) {
            log.error("Error calling Inventory Service: {}", e.getMessage());
            throw new ServiceUnavailableException("Inventory Service is unavailable", e);
        }
    }
}

