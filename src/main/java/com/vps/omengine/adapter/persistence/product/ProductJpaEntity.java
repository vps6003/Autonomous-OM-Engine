package com.vps.omengine.adapter.persistence.product;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "products", schema = "omengine")
public class ProductJpaEntity {

    @Id
    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    @Column(name = "description")
    private String description;

    @Column(name = "short_description")
    private String shortDescription;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected ProductJpaEntity() {
        // required by JPA
    }

    public ProductJpaEntity(
            UUID productId,
            String productName,
            String description,
            String shortDescription,
            BigDecimal price,
            String imageUrl,
            Integer stockQuantity,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.shortDescription = shortDescription;
        this.price = price;
        this.imageUrl = imageUrl;
        this.stockQuantity = stockQuantity;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getProductId() { return productId; }

    public String getProductName() { return productName; }

    public String getDescription() { return description; }

    public String getShortDescription() { return shortDescription; }

    public BigDecimal getPrice() { return price; }

    public Integer getStockQuantity() { return stockQuantity; }

    public String getImageUrl() { return imageUrl; }

    public Instant getCreatedAt() { return createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
}