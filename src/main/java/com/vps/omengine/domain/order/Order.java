package com.vps.omengine.domain.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Order {

    private final UUID orderId;
    private final UUID customerId;

    private List<OrderLine> orderLines;

    private OrderStatus status;

    private final Instant createdAt;
    private Instant updatedAt;

    private BigDecimal totalAmount;

    private long version;

    private Order(UUID customerId, List<OrderLine> orderLines) {

        this.orderId = UUID.randomUUID();
        this.customerId = customerId;

        this.orderLines = List.copyOf(orderLines);

        this.status = OrderStatus.CREATED;

        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;

        this.totalAmount = calculateTotal(orderLines);

        this.version = 0L;
    }

    public Order(
            UUID orderId,
            UUID customerId,
            List<OrderLine> orderLines,
            OrderStatus status,
            BigDecimal totalAmount,
            Instant createdAt,
            Instant updatedAt,
            long version
    ) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.orderLines = List.copyOf(orderLines);
        this.status = status;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public static Order create(UUID customerId, List<OrderLine> orderLines) {

        if (customerId == null) {
            throw new IllegalArgumentException("customerId cannot be null");
        }

        if (orderLines == null || orderLines.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one line");
        }

        return new Order(customerId, orderLines);
    }

    private BigDecimal calculateTotal(List<OrderLine> lines) {

        BigDecimal total = BigDecimal.ZERO;

        for (OrderLine line : lines) {
            total = total.add(line.getSubtotal());
        }

        return total;
    }

    private void transitionTo(OrderStatus next) {

        if (!status.canTransitionTo(next)) {
            throw new IllegalStateException(
                    "Invalid transition from " + status + " to " + next
            );
        }

        this.status = next;
        this.updatedAt = Instant.now();
        this.version++;
    }

    public void validate() {
        transitionTo(OrderStatus.VALIDATED);
    }

    public void confirm() {
        transitionTo(OrderStatus.CONFIRMED);
    }

    public void allocate() {
        transitionTo(OrderStatus.ALLOCATED);
    }

    public void startFulfillment() {
        transitionTo(OrderStatus.FULFILLING);
    }

    public void ship() {
        transitionTo(OrderStatus.SHIPPED);
    }

    public void deliver() {
        transitionTo(OrderStatus.DELIVERED);
    }

    public void cancel() {
        transitionTo(OrderStatus.CANCELLED);
    }

    public void fail() {
        transitionTo(OrderStatus.FAILED);
    }

    public void addOrderLine(OrderLine orderLine) {

        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException(
                    "Cannot modify order after creation stage"
            );
        }

        if (orderLine == null) {
            throw new IllegalArgumentException("OrderLine cannot be null");
        }

        List<OrderLine> updated = new ArrayList<>(orderLines);

        updated.add(orderLine);

        this.orderLines = List.copyOf(updated);

        this.totalAmount = calculateTotal(this.orderLines);

        this.updatedAt = Instant.now();
    }

    public UUID getOrderId() {
        return orderId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderLine> getOrderLines() {
        return orderLines;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public long getVersion() {
        return version;
    }
}