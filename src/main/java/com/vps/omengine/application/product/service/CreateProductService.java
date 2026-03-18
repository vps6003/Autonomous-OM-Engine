package com.vps.omengine.application.product.service;

import com.vps.omengine.application.product.dto.ProductResponse;
import com.vps.omengine.application.product.port.in.CreateProductUseCase;
import com.vps.omengine.application.product.port.out.ProductRepository;
import com.vps.omengine.domain.product.Product;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CreateProductService implements CreateProductUseCase {
    private final ProductRepository productRepository;

    public CreateProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    @Override
    public ProductResponse createProduct(
            String productName,
            String description,
            String shortDescription,
            String category,
            BigDecimal price,
            String imageUrl,
            Integer stockQuantity
    ){
        Product product = Product.create(
                productName,
                description,
                shortDescription,
                category,
                price,
                imageUrl,
                stockQuantity
        );

        Product savedProduct = productRepository.save(product);
        return mapToResponse(savedProduct);
    }

    private ProductResponse mapToResponse(Product product) {
        return new ProductResponse(
                product.getProductId(),
                product.getProductName(),
                product.getDescription(),
                product.getShortDescription(),
                product.getCategory(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getImageUrl(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
