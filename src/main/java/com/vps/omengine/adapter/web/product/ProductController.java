package com.vps.omengine.adapter.web.product;

import com.vps.omengine.application.product.dto.ProductResponse;
import com.vps.omengine.application.product.port.in.CreateProductUseCase;
import com.vps.omengine.application.product.port.in.GetProductUseCase;



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

    // spring injects the service implementations automatically
    public ProductController(
            CreateProductUseCase createProductUseCase,
            GetProductUseCase getProductUseCase
    ) {
        this.createProductUseCase = createProductUseCase;
        this.getProductUseCase = getProductUseCase;
    }

    // HTTP POST endpoint to create a product
    @PostMapping
    public ProductResponse createProduct(
            @RequestParam String productName,
            @RequestParam String description,
            @RequestParam String shortDescription,
            @RequestParam String category,
            @RequestParam BigDecimal price,
            @RequestParam String imageUrl,
            @RequestParam Integer stockQuantity
    ) {

        // delegate to application layer
        return createProductUseCase.createProduct(
                productName,
                description,
                shortDescription,
                category,
                price,
                imageUrl,
                stockQuantity
        );
    }

    // HTTP GET endpoint to fetch product by id
    @GetMapping("/{productId}")
    public ProductResponse getProduct(@PathVariable UUID productId) {

        return getProductUseCase.getProduct(productId);
    }

    // HTTP GET endpoint for paginated product list
    @GetMapping
    public List<ProductResponse> getProducts(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {

        return getProductUseCase.getProducts(page, size);
    }
}