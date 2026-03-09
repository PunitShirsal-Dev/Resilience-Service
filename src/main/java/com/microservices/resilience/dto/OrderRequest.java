package com.microservices.resilience.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private String orderId;
    private String productId;
    private Integer quantity;
    private Double amount;
    private String customerId;
}

