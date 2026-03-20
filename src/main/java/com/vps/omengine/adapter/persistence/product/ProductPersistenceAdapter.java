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
public class ProductPersistenceAdapter implements ProductRepository {

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

        // ✅ normalize
        String query = name.toLowerCase().trim();

        // ✅ remove numbers (iphone 16 → iphone)
        query = query.replaceAll("\\d+", "").trim();
        if (query.contains(" ")) {
            query = query.split(" ")[0];
        }

        // ✅ plural normalization
        if (query.endsWith("es")) {
            query = query.substring(0, query.length() - 2);
        } else if (query.endsWith("s")) {
            query = query.substring(0, query.length() - 1);
        }

        System.out.println("FINAL QUERY: " + query);

        // ✅ primary search
        var results = springDataproductRepository.searchFlexible(query);

        // 🔥 fallback (important)
        if (results.isEmpty()) {

            System.out.println("Fallback triggered for query: " + query);

            // fallback: try broader keyword
            if (query.contains("iphone")) {
                results = springDataproductRepository.searchFlexible("iphone");
            }

            // generic fallback: first page
            if (results.isEmpty()) {
                results = springDataproductRepository.findAll(PageRequest.of(0, 5)).getContent();
            }
        }

        // ✅ ranking (shorter name = better match)
        return results.stream()
                .sorted((a, b) -> Integer.compare(
                        a.getProductName().length(),
                        b.getProductName().length()
                ))
                .map(ProductMapper::toDomain)
                .toList();
    }
}