/*
package com.healthcare.catalog;

import com.healthcare.catalog.model.Product;
import com.healthcare.catalog.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
public class CatalogServiceTest {
    @Autowired
    private ProductRepository productRepository;

    @AfterEach
    void cleanUp() {
        productRepository.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveProduct() {
        Product product = new Product();
        product.setSku("TEST001");
        product.setName("Test Product");
        product.setPrice(BigDecimal.valueOf(19.99));

        Product saved = productRepository.save(product);
        Product found = productRepository.findById(saved.getId()).orElseThrow(() -> new RuntimeException("Product not found"));

        assertThat(found.getSku()).isEqualTo("TEST001");
    }
}*/
