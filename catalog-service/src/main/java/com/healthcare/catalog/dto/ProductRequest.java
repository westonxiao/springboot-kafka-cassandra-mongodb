package com.healthcare.catalog.dto;

import javax.validation.constraints.*;
import java.math.BigDecimal;

public class ProductRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String category;

    @Positive
    private BigDecimal price;

    private boolean inStock;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public boolean isInStock() { return inStock; }
    public void setInStock(boolean inStock) { this.inStock = inStock; }
}