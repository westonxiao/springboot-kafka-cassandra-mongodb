package com.healthcare.catalog.service;

import com.healthcare.catalog.dto.PriceUpdate;
import com.healthcare.catalog.model.Product;
import com.healthcare.catalog.repository.ProductRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(String id) {
        return productRepository.findById(id);
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public Product updateProduct(String id, Product productDetails) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    existingProduct.setName(productDetails.getName());
                    existingProduct.setDescription(productDetails.getDescription());
                    existingProduct.setPrice(productDetails.getPrice());
                    existingProduct.setStockQuantity(productDetails.getStockQuantity());
                    existingProduct.setAttributes(productDetails.getAttributes());
                    return productRepository.save(existingProduct);
                })
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }

    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword);
    }

    @Async("catalogTaskExecutor")
    public CompletableFuture<List<Product>> asyncSearchProducts(String query) {
        return CompletableFuture.completedFuture(
                productRepository.findByNameContainingIgnoreCase(query)
        );
    }

    @Async
    public void asyncUpdatePrices(List<PriceUpdate> updates) {
        updates.forEach(update -> {
            productRepository.updatePrice(update.getSku(), update.getNewPrice());
        });
    }

}