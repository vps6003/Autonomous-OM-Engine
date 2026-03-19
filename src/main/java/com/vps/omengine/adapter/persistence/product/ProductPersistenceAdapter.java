package com.vps.omengine.adapter.persistence.product;

import com.vps.omengine.application.product.port.out.ProductRepository;
import com.vps.omengine.domain.product.Product;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Component
public  class ProductPersistenceAdapter implements ProductRepository {

    private final SpringDataProductRepository springDataproductRepository;

    public ProductPersistenceAdapter(SpringDataProductRepository springDataproductRepository) {
        this.springDataproductRepository = springDataproductRepository;
    }

    @Override
    public Product save(Product product) {

        ProductJpaEntity entity = ProductMapper.toJpaEntity(product);

        ProductJpaEntity savedEntity = springDataproductRepository.save(entity);

        return ProductMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Product> findById(UUID productId) {

        return springDataproductRepository
                .findById(productId)
                .map(ProductMapper::toDomain);
    }

    @Override
    public List<Product> findAll(Integer page, Integer size) {

        Pageable pageable = PageRequest.of(page, size);

        return springDataproductRepository
                .findAll(pageable)
                .stream()
                .map(ProductMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID productId) {
        springDataproductRepository.deleteById(productId);
    }

    @Override
    public List<Product> searchByName(String name) {

        return springDataproductRepository
                .findByProductNameContainingIgnoreCase(name)
                .stream()
                .map(ProductMapper::toDomain)
                .toList();
    }
}