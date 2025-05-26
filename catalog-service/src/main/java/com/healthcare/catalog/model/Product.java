package com.healthcare.catalog.model;

import org.springframework.data.cassandra.core.mapping.*;
import java.math.BigDecimal;
import java.util.UUID;

@Table("products")
public class Product {
    @PrimaryKey
    private UUID id;

    @Column("name")
    private String name;

    @Column("category")
    private String category;

    @Column("price")
    private BigDecimal price;

    @Column("in_stock")
    private boolean inStock;

    // Constructors
    public Product() {
        this.id = UUID.randomUUID();
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public boolean isInStock() { return inStock; }
    public void setInStock(boolean inStock) { this.inStock = inStock; }
}