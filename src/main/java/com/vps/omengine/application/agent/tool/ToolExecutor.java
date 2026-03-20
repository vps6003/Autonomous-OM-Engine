package com.vps.omengine.application.agent.tool;

import com.fasterxml.jackson.databind.JsonNode;
import com.vps.omengine.application.order.port.in.CreateOrderCommand;
import com.vps.omengine.application.order.service.CreateOrderService;
import com.vps.omengine.application.product.port.out.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ToolExecutor {

    private final ProductRepository productRepository;
    private final CreateOrderService orderService;

    public Object execute(String tool, JsonNode input) {

        switch (tool) {
            case "search_products" -> {

                String query = input.get("query").asText()
                        .toLowerCase()
                        .trim();

                // ✅ remove numbers (iphone 16 → iphone)
                query = query.replaceAll("\\d+", "").trim();

                // ✅ plural normalization
                if (query.endsWith("es")) {
                    query = query.substring(0, query.length() - 2);
                } else if (query.endsWith("s")) {
                    query = query.substring(0, query.length() - 1);
                }

                System.out.println("FINAL SEARCH QUERY: " + query);

                var products = productRepository.searchByName(query);

                return Map.of(
                        "type", "PRODUCT_SEARCH_RESULT",
                        "data", products.stream().map(p -> Map.of(
                                "productId", p.getProductId(),
                                "name", p.getProductName(),
                                "price", p.getPrice(),
                                "stock", p.getStockQuantity()
                        )).toList()
                );
            }
            case "create_order" -> {
                try {

                    UUID customerId = UUID.fromString("11111111-1111-1111-1111-111111111111");

                    UUID productId = UUID.fromString(input.get("productId").asText());
                    int quantity = input.get("quantity").asInt();

                    var command = new CreateOrderCommand(
                            customerId,
                            List.of(new CreateOrderCommand.OrderItem(productId, quantity, null))
                    );

                    var response = orderService.createOrder(command);

                    return Map.of(
                            "type", "ORDER_SUCCESS",
                            "data", Map.of(
                                    "orderId", response.orderId(),
                                    "total", response.totalAmount()
                            )
                    );

                } catch (Exception e) {
                    return Map.of(
                            "type", "ORDER_FAILED",
                            "message", e.getMessage()
                    );
                }
            }

            default -> {
                return Map.of(
                        "type", "UNKNOWN_TOOL",
                        "message", "Unknown tool: " + tool
                );
            }
        }
    }
}