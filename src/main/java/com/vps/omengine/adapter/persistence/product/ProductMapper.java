package com.vps.omengine.adapter.persistence.product;

import com.vps.omengine.domain.product.Product;

public final class ProductMapper {

    private ProductMapper() {
        // prevent instantiation
    }

    public static ProductJpaEntity toJpaEntity(Product product) {

        return new ProductJpaEntity(
                product.getProductId(),
                product.getProductName(),
                product.getDescription(),
                product.getShortDescription(),
                product.getPrice(),
                product.getImageUrl(),
                product.getStockQuantity(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    public static Product toDomain(ProductJpaEntity entity) {

        return Product.rehydrate(
                entity.getProductId(),
                entity.getProductName(),
                entity.getDescription(),
                entity.getShortDescription(),
                entity.getPrice(),
                entity.getImageUrl(),
                entity.getStockQuantity(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}