    package com.vps.omengine.application.agent.tool.impl;

    import com.vps.omengine.application.product.port.out.ProductRepository;
    import com.vps.omengine.domain.product.Product;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Component;

    import java.util.List;

    @Component
    @RequiredArgsConstructor
    public class SearchProductTool {

        private final ProductRepository productRepository;

        public String execute(String input) {
            String query = input.toLowerCase().trim();

            if (query.endsWith("es")) {
                query = query.substring(0, query.length() - 2);
            } else if (query.endsWith("s")) {
                query = query.substring(0, query.length() - 1);
            }

            List<Product> products = productRepository.searchByName(query);

            if (products.isEmpty()) {
                return "No products found for query: " + input;
            }

            return products.stream()
                    .map(p -> p.getProductName() + " - ₹" + p.getPrice())
                    .toList()
                    .toString();
        }

    }