package com.vps.omengine.application.agent.tool.impl;

import com.vps.omengine.application.order.port.in.CreateOrderCommand;
import com.vps.omengine.application.order.port.in.CreateOrderUseCase;
import com.vps.omengine.application.product.port.out.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CreateOrderTool {

    private final ProductRepository productRepository;
    private final CreateOrderUseCase createOrderUseCase;

    public String execute(String input) {

        try {
            String[] parts = input.split(" ");

            int quantity = Integer.parseInt(parts[1]);
            String productName = parts[2];

            var product = productRepository.findAll(0, 10).stream()
                    .filter(p -> p.getProductName().equalsIgnoreCase(productName))
                    .findFirst()
                    .orElseThrow();

            var orderItem = new CreateOrderCommand.OrderItem(
                    product.getProductId(),
                    quantity,
                    product.getPrice()
            );
            // dummy id for now
            UUID customerId = UUID.randomUUID();
            var command = new CreateOrderCommand(customerId,List.of(orderItem));

            var response = createOrderUseCase.createOrder(command);

            return "Order created: " + response;

        } catch (Exception e) {
            return "Order failed: " + e.getMessage();
        }
    }
}