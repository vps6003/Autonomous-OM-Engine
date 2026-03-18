package com.vps.omengine.application.order.port.out;

import com.vps.omengine.domain.order.Order;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(UUID orderId);
}
