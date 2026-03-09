package com.microservices.resilience.client;

import com.microservices.resilience.dto.OrderRequest;
import com.microservices.resilience.dto.OrderResponse;
import com.microservices.resilience.exception.ServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderServiceClient {

    private final WebClient webClient;

    @Value("${external.services.order.url}")
    private String orderServiceUrl;

    public OrderResponse createOrder(OrderRequest request) {
        log.info("Calling Order Service to create order: {}", request.getOrderId());

        try {
            return webClient.post()
                    .uri(orderServiceUrl)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(OrderResponse.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();
        } catch (Exception e) {
            log.error("Error calling Order Service: {}", e.getMessage());
            throw new ServiceUnavailableException("Order Service is unavailable", e);
        }
    }

    public OrderResponse getOrder(String orderId) {
        log.info("Calling Order Service to get order: {}", orderId);

        try {
            return webClient.get()
                    .uri(orderServiceUrl + "/" + orderId)
                    .retrieve()
                    .bodyToMono(OrderResponse.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();
        } catch (Exception e) {
            log.error("Error calling Order Service: {}", e.getMessage());
            throw new ServiceUnavailableException("Order Service is unavailable", e);
        }
    }
}

