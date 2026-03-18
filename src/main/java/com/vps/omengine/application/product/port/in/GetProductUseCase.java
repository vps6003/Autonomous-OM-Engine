package com.vps.omengine.application.product.port.in;

import com.vps.omengine.application.product.dto.ProductResponse;

import java.util.List;
import java.util.UUID;

public interface GetProductUseCase {

    // fetch product by its ID
    ProductResponse getProduct(UUID productId);

    // fetch paginated list of products
    List<ProductResponse> getProducts(Integer page, Integer size);
}