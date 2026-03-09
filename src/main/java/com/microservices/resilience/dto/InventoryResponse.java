package com.microservices.resilience.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse {
    private String productId;
    private Integer availableQuantity;
    private Boolean inStock;
    private String message;
}

