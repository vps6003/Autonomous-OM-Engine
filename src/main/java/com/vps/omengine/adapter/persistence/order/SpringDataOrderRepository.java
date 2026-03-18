package com.vps.omengine.adapter.persistence.order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataOrderRepository
        extends JpaRepository<OrderJpaEntity, UUID> {
}
