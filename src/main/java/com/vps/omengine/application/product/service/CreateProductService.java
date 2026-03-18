package com.vps.omengine.application.product.service;

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
    public Product createProduct(
            String productName,
            String description,
            String shortDescription,
            BigDecimal price,
            String imageUrl,
            Integer stockQuantity
    ){
        Product product = Product.create(
                productName,
                description,
                shortDescription,
                price,
                imageUrl,
                stockQuantity
        );

        return productRepository.save(product);
    }
}
