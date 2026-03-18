package com.vps.omengine.application.product.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductResponse(
        UUID productId,
        String productName,
        String description,
        String shortDescription,
        String category,
        BigDecimal price,
        Integer stockQuantity,
        String imageUrl,
        Instant createdAt,
        Instant updatedAt
) {}