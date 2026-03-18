package com.vps.omengine.adapter.persistence.order;

import com.vps.omengine.application.order.port.out.OrderRepository;
import com.vps.omengine.domain.order.Order;
import com.vps.omengine.domain.order.OrderStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class OrderPersistenceAdapter implements OrderRepository {

    private final SpringDataOrderRepository repository;

    public OrderPersistenceAdapter(SpringDataOrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public Order save(Order order) {

        OrderJpaEntity entity = toEntity(order);

        repository.save(entity);

        return order;
    }

    @Override
    public Optional<Order> findById(UUID orderId) {

        return repository.findById(orderId)
                .map(this::toDomain);
    }

    private OrderJpaEntity toEntity(Order order) {

        OrderJpaEntity entity = new OrderJpaEntity();

        entity.setOrderId(order.getOrderId());
        entity.setCustomerId(order.getCustomerId());
        entity.setStatus(order.getStatus().name());
        entity.setTotalAmount(order.getTotalAmount());
        entity.setCreatedAt(order.getCreatedAt());
        entity.setUpdatedAt(order.getUpdatedAt());
        entity.setVersion(order.getVersion());

        return entity;
    }

    private Order toDomain(OrderJpaEntity entity) {

        return new Order(
                entity.getOrderId(),
                entity.getCustomerId(),
                List.of(),// lines later
                OrderStatus.valueOf(entity.getStatus()),
                entity.getTotalAmount(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion()
        );
    }
}