package com.microservices.resilience.controller;

import com.microservices.resilience.dto.InventoryResponse;
import com.microservices.resilience.service.TimeoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/resilience/timeout")
@RequiredArgsConstructor
@Slf4j
public class TimeoutController {

    private final TimeoutService timeoutService;

    @GetMapping("/inventory/{productId}")
    public ResponseEntity<InventoryResponse> checkInventory(@PathVariable String productId,
                                                            @RequestParam Integer quantity) throws ExecutionException, InterruptedException {
        log.info("Timeout: Check inventory request received: {}", productId);
        return ResponseEntity.ok(timeoutService.checkInventoryWithTimeout(productId, quantity).get());
    }
}

