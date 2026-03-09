package com.microservices.resilience.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private String paymentId;
    private String orderId;
    private Double amount;
    private String paymentMethod;
}

