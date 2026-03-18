package com.vps.omengine.application.order.port.in;

import com.vps.omengine.application.order.dto.OrderResponse;

import java.util.UUID;

public interface CreateOrderUseCase {

    OrderResponse createOrder(CreateOrderCommand command);

}