package com.vps.omengine.application.order.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(        UUID orderId,
        UUID customerId,
        List<OrderLineResponse> items,
        BigDecimal totalAmount,
        String status,
        Instant createdAt
) {
    public record OrderLineResponse(
            UUID productId,
            int quantity,
            BigDecimal price,
            BigDecimal subtotal
    ) {}
}