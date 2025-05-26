package com.healthcare.catalog.repository;

import com.healthcare.catalog.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product, String> {
    Optional<Product> findBySku(String sku);

    List<Product> findByCategory(String category);

    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    @Query("{'attributes.$**': ?0}")
    List<Product> findByAttribute(String key, Object value);

    List<Product> findByActiveIsTrue();

    @Query("{$or: ["
            + "{'name': {$regex: ?0, $options: 'i'}}, "
            + "{'description': {$regex: ?0, $options: 'i'}}"
            + "]}")
    List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String keyword);

    //for multi-threading
    // Add these new methods:
    List<Product> findByNameContainingIgnoreCase(String name);

    @Query("{'price': {$gte: ?0, $lte: ?1}}")
    List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

    @Query(value = "{'sku': ?0}", fields = "{'price': 1}")
    Optional<Product> findPriceBySku(String sku);

    @Update("{'$set': {'price': ?1}}")
    void updatePrice(String sku, BigDecimal newPrice);
}