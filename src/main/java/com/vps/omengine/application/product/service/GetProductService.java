package com.vps.omengine.application.product.service;

import com.vps.omengine.application.product.dto.ProductResponse;
import com.vps.omengine.application.product.port.in.GetProductUseCase;
import com.vps.omengine.application.product.port.out.ProductRepository;
import com.vps.omengine.domain.product.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GetProductService implements GetProductUseCase {
    // repository output port
    private final ProductRepository productRepository;

    public GetProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    @Override
    public ProductResponse getProduct(UUID productId){
        // fetch product from persistence layer
        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Product not found : "+ productId)
                );

        return mapToResponse(product);
    }

    @Override
    public List<ProductResponse> getProducts(Integer page, Integer size){
        return productRepository.findAll(page, size)
                .stream()
                .map(this::mapToResponse)
                .toList();
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
