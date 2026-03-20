package com.vps.omengine.adapter.persistence.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataProductRepository extends JpaRepository<ProductJpaEntity, UUID> {

    List<ProductJpaEntity> findByProductNameContainingIgnoreCase(String name);

    @Query("""
    SELECT p FROM ProductJpaEntity p
    WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :query, '%'))
""")
    List<ProductJpaEntity> searchFlexible(@Param("query") String query);
}