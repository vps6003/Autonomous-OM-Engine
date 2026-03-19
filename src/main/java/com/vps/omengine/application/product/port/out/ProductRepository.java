package com.vps.omengine.application.product.port.out;

import com.vps.omengine.domain.product.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(UUID productId);

    List<Product> findAll(Integer page , Integer size);

    void deleteById(UUID productId);

    List<Product> searchByName(String name);

}
