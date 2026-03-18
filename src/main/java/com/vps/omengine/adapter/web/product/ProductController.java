package com.vps.omengine.adapter.web.product;

import com.vps.omengine.application.product.port.in.CreateProductUseCase;
import com.vps.omengine.application.product.port.in.GetProductUseCase;

import com.vps.omengine.domain.product.Product;

import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {

    // input ports of application layer
    private final CreateProductUseCase createProductUseCase;
    private final GetProductUseCase getProductUseCase;
    private final GetProductUseCase getProductsUseCase;

    // spring injects the service implementations automatically
    public ProductController(
            CreateProductUseCase createProductUseCase,
            GetProductUseCase getProductUseCase,
            GetProductUseCase getProductsUseCase
    ) {
        this.createProductUseCase = createProductUseCase;
        this.getProductUseCase = getProductUseCase;
        this.getProductsUseCase = getProductsUseCase;
    }

    // HTTP POST endpoint to create a product
    @PostMapping
    public Product createProduct(
            @RequestParam String productName,
            @RequestParam String description,
            @RequestParam String shortDescription,
            @RequestParam BigDecimal price,
            @RequestParam String imageUrl,
            @RequestParam Integer stockQuantity
    ) {

        // delegate to application layer
        return createProductUseCase.createProduct(
                productName,
                description,
                shortDescription,
                price,
                imageUrl,
                stockQuantity
        );
    }

    // HTTP GET endpoint to fetch product by id
    @GetMapping("/{productId}")
    public Product getProduct(@PathVariable UUID productId) {

        return getProductUseCase.getProduct(productId);
    }

    // HTTP GET endpoint for paginated product list
    @GetMapping
    public List<Product> getProducts(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {

        return getProductsUseCase.getProducts(page, size);
    }
}