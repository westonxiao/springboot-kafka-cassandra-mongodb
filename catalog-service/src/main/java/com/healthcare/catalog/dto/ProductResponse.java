package com.healthcare.catalog.dto;

import com.healthcare.catalog.model.Product;
import java.math.BigDecimal;
import java.util.UUID;

public class ProductResponse {
    private UUID id;
    private String name;
    private String category;
    private BigDecimal price;
    private boolean inStock;

    public static ProductResponse fromEntity(Product product) {
        ProductResponse response = new ProductResponse();
        response.id = product.getId();
        response.name = product.getName();
        response.category = product.getCategory();
        response.price = product.getPrice();
        response.inStock = product.isInStock();
        return response;
    }

    // Getters
    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public BigDecimal getPrice() { return price; }
    public boolean isInStock() { return inStock; }
}