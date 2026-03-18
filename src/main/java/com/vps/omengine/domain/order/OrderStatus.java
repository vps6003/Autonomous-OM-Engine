package com.vps.omengine.domain.order;

public enum OrderStatus {

    CREATED,
    VALIDATED,
    CONFIRMED,
    ALLOCATED,
    FULFILLING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    FAILED;

    public boolean isTerminal() {
        return this == DELIVERED
                || this == CANCELLED
                || this == FAILED;
    }

    public boolean canTransitionTo(OrderStatus next) {

        if (next == null) {
            throw new IllegalArgumentException("Next status cannot be null");
        }

        switch (this) {

            case CREATED:
                return next == VALIDATED || next == FAILED;

            case VALIDATED:
                return next == CONFIRMED || next == CANCELLED || next == FAILED;

            case CONFIRMED:
                return next == ALLOCATED || next == CANCELLED;

            case ALLOCATED:
                return next == FULFILLING || next == CANCELLED || next == FAILED;

            case FULFILLING:
                return next == SHIPPED || next == FAILED;

            case SHIPPED:
                return next == DELIVERED;

            case DELIVERED:
            case CANCELLED:
            case FAILED:
                return false;

            default:
                return false;
        }
    }
}