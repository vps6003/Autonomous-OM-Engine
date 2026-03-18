package com.vps.omengine.application.order.port.in;

import java.util.UUID;

public interface CreateOrderUseCase {

    UUID createOrder(CreateOrderCommand command);

}