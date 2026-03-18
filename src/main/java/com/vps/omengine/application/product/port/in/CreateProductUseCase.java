package com.vps.omengine.application.product.port.in;

import com.vps.omengine.application.product.dto.ProductResponse;

import java.math.BigDecimal;

public interface CreateProductUseCase {

    ProductResponse createProduct(
            String productName,
            String description,
            String shortDescription,
            String category,
            BigDecimal price,
            String imageUrl,
            Integer stockQuantity
    );
}
