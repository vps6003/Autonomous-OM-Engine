CREATE SCHEMA IF NOT EXISTS omengine;

-- Orders Table
CREATE TABLE omengine.orders
(
    order_id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    total_amount NUMERIC(19,2) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL
);

-- Order Lines Table
CREATE TABLE omengine.order_lines
(
    id BIGSERIAL PRIMARY KEY,
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity INT NOT NULL,
    price NUMERIC(19,2) NOT NULL,

    CONSTRAINT fk_order
        FOREIGN KEY (order_id)
            REFERENCES omengine.orders(order_id)
            ON DELETE CASCADE
);