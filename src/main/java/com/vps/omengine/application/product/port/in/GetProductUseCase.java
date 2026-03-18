package com.vps.omengine.application.product.port.in;

import com.vps.omengine.domain.product.Product;

import java.util.List;
import java.util.UUID;

public interface GetProductUseCase {

    // fetch product by its ID
    Product getProduct(UUID productId);

    // fetch paginated list of products
    List<Product> getProducts(Integer page, Integer size);
}