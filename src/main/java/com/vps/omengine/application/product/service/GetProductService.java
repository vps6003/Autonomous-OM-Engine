package com.vps.omengine.application.product.service;

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
    public Product getProduct(UUID productId){
        // fetch product from persistence layer
        return productRepository.findById(productId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Product not found : "+ productId)
                );
    }

    @Override
    public List<Product> getProducts(Integer page, Integer size){
        return productRepository.findAll(page,size);
    }
}
