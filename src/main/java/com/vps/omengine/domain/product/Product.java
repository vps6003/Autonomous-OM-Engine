    package com.vps.omengine.domain.product;

    import java.math.BigDecimal;
    import java.time.Instant;
    import java.util.UUID;

    public class Product {

        private final UUID productId;

        private String productName;
        private String description;
        private String shortDescription;
        private String category;

        private BigDecimal price;

        private Integer stockQuantity;

        private String imageUrl;

        private final Instant createdAt;
        private Instant updatedAt;

        private Product(
                UUID productId,
                String productName,
                String description,
                String shortDescription,
                String category,
                BigDecimal price,
                String imageUrl,
                Integer stockQuantity,
                Instant createdAt,
                Instant updatedAt
        ){
            if(productId == null)
                throw new IllegalArgumentException("ProductId cannot be null");

            if(productName == null || productName.isBlank())
                throw new IllegalArgumentException("Product name cannot be empty");

            if(price == null || price.compareTo(BigDecimal.ZERO) < 0)
                throw new IllegalArgumentException("Price must be positive");

            if(stockQuantity == null || stockQuantity < 0)
                throw new IllegalArgumentException("Stock cannot be negative");

            if(createdAt == null)
                throw new IllegalArgumentException("createdAt cannot be null");

            if(updatedAt == null)
                throw new IllegalArgumentException("updatedAt cannot be null");

            this.productId = productId;
            this.productName = productName;
            this.shortDescription = shortDescription;
            this.description = description;
            this.category = category;
            this.price = price;
            this.stockQuantity = stockQuantity;
            this.imageUrl = imageUrl;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;

        }

        public UUID getProductId() {
            return productId;
        }

        public String getProductName() {
            return productName;
        }

        public String getShortDescription() {
            return shortDescription;
        }

        public String getDescription() {
            return description;
        }

        public String getCategory() { return category;}

        public BigDecimal getPrice() {
            return price;
        }

        public Integer getStockQuantity() {
            return stockQuantity;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public Instant getCreatedAt() {
            return createdAt;
        }

        public Instant getUpdatedAt() {
            return updatedAt;
        }


        public void updatePrice(BigDecimal newPrice){
            if(newPrice == null || newPrice.compareTo(BigDecimal.ZERO) < 0)
                throw new IllegalArgumentException("Price must be positive.");

            this.price  = newPrice;
            this.updatedAt = Instant.now();
        }

        public void increaseStock(int quantity) {
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }

            this.stockQuantity += quantity;
            this.updatedAt = Instant.now();
        }

        public void decreaseStock(int quantity) {
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }

            if (this.stockQuantity < quantity) {
                throw new IllegalStateException("Insufficient stock");
            }

            this.stockQuantity -= quantity;
            this.updatedAt = Instant.now();
        }

        public boolean isInStock(int requiredQuantity) {
            if (requiredQuantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            return this.stockQuantity >= requiredQuantity;
        }

        public static Product create(
                String productName,
                String description,
                String shortDescription,
                String category,
                BigDecimal price,
                String imageUrl,
                Integer stockQuantity
        ){
            Instant now = Instant.now();

            return new Product(
                    UUID.randomUUID(),
                    productName,
                    description,
                    shortDescription,
                    category,
                    price,
                    imageUrl,
                    stockQuantity,
                    now,
                    now
            );
        }
        public static Product rehydrate(
                UUID productId,
                String productName,
                String description,
                String shortDescription,
                String category,
                BigDecimal price,
                String imageUrl,
                Integer stockQuantity,
                Instant createdAt,
                Instant updatedAt
        ){
            return new Product(
                    productId,
                    productName,
                    description,
                    shortDescription,
                    category,
                    price,
                    imageUrl,
                    stockQuantity,
                    createdAt,
                    updatedAt
            );
        }



    }
