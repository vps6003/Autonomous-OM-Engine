package com.vps.omengine.application.order.port.in;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateOrderCommand(

        UUID customerId,
        List<OrderItem> items

) {

    public record OrderItem(
            UUID productId,
            int quantity,
            BigDecimal price
    ) {}

}