package com.microservices.resilience.client;

import com.microservices.resilience.dto.PaymentRequest;
import com.microservices.resilience.dto.PaymentResponse;
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
public class PaymentServiceClient {

    private final WebClient webClient;

    @Value("${external.services.payment.url}")
    private String paymentServiceUrl;

    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("Calling Payment Service to process payment: {}", request.getPaymentId());

        try {
            return webClient.post()
                    .uri(paymentServiceUrl)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(PaymentResponse.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();
        } catch (Exception e) {
            log.error("Error calling Payment Service: {}", e.getMessage());
            throw new ServiceUnavailableException("Payment Service is unavailable", e);
        }
    }

    public PaymentResponse getPayment(String paymentId) {
        log.info("Calling Payment Service to get payment: {}", paymentId);

        try {
            return webClient.get()
                    .uri(paymentServiceUrl + "/" + paymentId)
                    .retrieve()
                    .bodyToMono(PaymentResponse.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();
        } catch (Exception e) {
            log.error("Error calling Payment Service: {}", e.getMessage());
            throw new ServiceUnavailableException("Payment Service is unavailable", e);
        }
    }
}

