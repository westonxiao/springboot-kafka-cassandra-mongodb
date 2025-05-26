package com.healthcare.catalog.repository;

import com.healthcare.catalog.model.Product;
import org.springframework.data.cassandra.repository.*;
import java.util.*;

public interface ProductRepository extends CassandraRepository<Product, UUID> {
    @Query("SELECT * FROM products WHERE category = ?0 ALLOW FILTERING")
    List<Product> findByCategory(String category);

    @Query("SELECT * FROM products WHERE in_stock = true ALLOW FILTERING")
    List<Product> findAvailableProducts();
}