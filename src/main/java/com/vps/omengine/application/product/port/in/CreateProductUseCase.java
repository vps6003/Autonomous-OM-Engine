package com.vps.omengine.application.product.port.in;

import com.vps.omengine.domain.product.Product;

import java.math.BigDecimal;

public interface CreateProductUseCase {

    Product createProduct(
            String productName,
            String description,
            String shortDescription,
            BigDecimal price,
            String imageUrl,
            Integer stockQuantity
    );
}
