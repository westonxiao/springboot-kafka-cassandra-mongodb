package com.healthcare.catalog.service;

import com.healthcare.catalog.dto.ProductRequest;
import com.healthcare.catalog.dto.ProductResponse;
import com.healthcare.catalog.model.Product;
import com.healthcare.catalog.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public ProductResponse createProduct(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setInStock(request.isInStock());

        Product saved = repository.save(product);
        return ProductResponse.fromEntity(saved);
    }

    public List<ProductResponse> getProductsByCategory(String category) {
        return repository.findByCategory(category)
                .stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }
}