package com.vps.omengine.application.order.service;

import com.vps.omengine.application.order.dto.OrderResponse;
import com.vps.omengine.application.order.port.in.CreateOrderCommand;
import com.vps.omengine.application.order.port.in.CreateOrderUseCase;
import com.vps.omengine.application.order.port.out.OrderRepository;
import com.vps.omengine.application.product.port.out.ProductRepository;
import com.vps.omengine.domain.order.Order;
import com.vps.omengine.domain.order.OrderLine;
import com.vps.omengine.domain.product.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CreateOrderService implements CreateOrderUseCase {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public CreateOrderService(
            OrderRepository orderRepository,
            ProductRepository productRepository
    ){
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    @Override
    public OrderResponse createOrder(CreateOrderCommand command){

        // 🔹 Step 1: Convert items → OrderLines (via helper method)
        List<OrderLine> lines = command.items()
                .stream()
                .map(this::processOrderItem)   // 👈 clean delegation
                .collect(Collectors.toList());

        // 🔹 Step 2: Create Order aggregate
        Order order  = Order.create(command.customerId(), lines);

        // 🔹 Step 3: Persist Order
        orderRepository.save(order);

        return mapToResponse(order);
    }

    /**
     * 🔹 Helper method (kept INSIDE SAME CLASS)
     * Handles:
     * - Product fetch
     * - Stock validation
     * - Stock deduction
     * - OrderLine creation
     */
    private OrderLine processOrderItem(CreateOrderCommand.OrderItem item) {

        // 1. Fetch product
        Product product = productRepository.findById(item.productId())
                .orElseThrow(() ->
                        new RuntimeException("Product not found: " + item.productId())
                );

        // 2. Validate stock
        if (!product.isInStock(item.quantity())) {
            throw new RuntimeException(
                    "Insufficient stock for product: " + item.productId()
            );
        }

        // 3. Deduct stock
        product.decreaseStock(item.quantity());

        // 4. Persist updated product
        productRepository.save(product);

        // 5. Create OrderLine using product price (NOT request price)
        return OrderLine.create(
                item.productId(),
                item.quantity(),
                product.getPrice()
        );
    }
    private OrderResponse mapToResponse(Order order) {

        List<OrderResponse.OrderLineResponse> items =
                order.getOrderLines().stream()
                        .map(line -> new OrderResponse.OrderLineResponse(
                                line.getProductId(),
                                line.getQuantity(),
                                line.getPrice(),
                                line.getSubtotal()
                        ))
                        .toList();

        return new OrderResponse(
                order.getOrderId(),
                order.getCustomerId(),
                items,
                order.getTotalAmount(),
                order.getStatus().name(),
                order.getCreatedAt()
        );
    }
}