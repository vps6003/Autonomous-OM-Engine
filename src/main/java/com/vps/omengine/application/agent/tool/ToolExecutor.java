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

                return productRepository.searchByName(query)
                        .stream()
                        .map(p -> "Product{id=" + p.getProductId()
                                + ", name=" + p.getProductName()
                                + ", price=" + p.getPrice()
                                + ", stock=" + p.getStockQuantity() + "}")
                        .toList()
                        .toString();
            }
            case "create_order" -> {

                try {

                    UUID customerId = UUID.fromString("11111111-1111-1111-1111-111111111111");

                    List<CreateOrderCommand.OrderItem> items;

                    // 🔥 CASE 1: MULTIPLE ITEMS
                    if (input.has("items")) {

                        items = new ArrayList<>();

                        for (JsonNode itemNode : input.get("items")) {

                            UUID productId = UUID.fromString(itemNode.get("productId").asText());
                            int quantity = itemNode.get("quantity").asInt();

                            if (quantity <= 0) {
                                return "ORDER_FAILED: Invalid quantity for product " + productId;
                            }

                            items.add(new CreateOrderCommand.OrderItem(productId, quantity, null));
                        }

                    }
                    // 🔥 CASE 2: SINGLE ITEM (BACKWARD COMPATIBLE)
                    else {

                        if (!input.has("productId") || !input.has("quantity")) {
                            return "ORDER_FAILED: Invalid input";
                        }

                        UUID productId = UUID.fromString(input.get("productId").asText());
                        int quantity = input.get("quantity").asInt();

                        if (quantity <= 0) {
                            return "ORDER_FAILED: Invalid quantity";
                        }

                        items = List.of(
                                new CreateOrderCommand.OrderItem(productId, quantity, null)
                        );
                    }

                    // 🔥 Build command
                    CreateOrderCommand command =
                            new CreateOrderCommand(customerId, items);

                    var response = orderService.createOrder(command);

                    return "ORDER_SUCCESS: orderId=" + response.orderId()
                            + ", total=" + response.totalAmount();

                } catch (Exception e) {

                    String pid = input.has("productId") ? input.get("productId").asText() : "multiple";

                    return "ORDER_FAILED: " + e.getMessage() + " (productId=" + pid + ")";
                }
            }


            default -> {
                return "Unknown tool: " + tool;
            }
        }
    }
}