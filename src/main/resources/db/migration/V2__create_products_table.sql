CREATE  TABLE  omengine.products(
    product_id UUID PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL ,
    description TEXT,
    short_description TEXT,
    category VARCHAR(100) NOT NULL,
    price NUMERIC(19,2) NOT NULL ,
    image_url TEXT,
    stock_quantity INT NOT NULL,
    created_at TIMESTAMP NOT NULL ,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);