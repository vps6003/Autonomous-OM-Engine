package com.vps.omengine.application.agent.tool;

import com.fasterxml.jackson.databind.JsonNode;
import com.vps.omengine.application.order.port.in.CreateOrderCommand;
import com.vps.omengine.application.order.service.CreateOrderService;
import com.vps.omengine.application.product.port.out.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ToolExecutor {

    private final ProductRepository productRepository;
    private final CreateOrderService orderService;

    public String execute(String tool, JsonNode input) {

        switch (tool) {

            case "search_products" -> {
                String query = input.asText();

                return "PRODUCT_SEARCH_RESULT: " +
                        productRepository.searchByName(query)
                                .stream()
                                .map(p -> """
                                        {
                                          "productId": "%s",
                                          "name": "%s",
                                          "price": %s,
                                          "stock": %s
                                        }
                                        """.formatted(
                                        p.getProductId(),
                                        p.getProductName(),
                                        p.getPrice(),
                                        p.getStockQuantity()
                                ))
                                .toList();
            }

            case "create_order" -> {
                try {

                    UUID customerId = UUID.fromString("11111111-1111-1111-1111-111111111111");

                    List<CreateOrderCommand.OrderItem> items = new ArrayList<>();

                    UUID productId = UUID.fromString(input.get("productId").asText());
                    int quantity = input.get("quantity").asInt();

                    items.add(new CreateOrderCommand.OrderItem(productId, quantity, null));

                    CreateOrderCommand command =
                            new CreateOrderCommand(customerId, items);

                    var response = orderService.createOrder(command);

                    return "ORDER_SUCCESS: orderId=" + response.orderId()
                            + ", total=" + response.totalAmount();

                } catch (Exception e) {
                    return "ORDER_FAILED: " + e.getMessage();
                }
            }

            default -> {
                return "Unknown tool: " + tool;
            }
        }
    }
}