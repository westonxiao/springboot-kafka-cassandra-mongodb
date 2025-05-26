package com.healthcare.catalog.service;

import com.healthcare.catalog.model.Product;
import com.healthcare.catalog.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
public class ProductImportService {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private Executor catalogTaskExecutor;

    public void importProducts(List<Product> products) {
        List<CompletableFuture<Void>> futures = products.stream()
                .map(product -> CompletableFuture.runAsync(
                        () -> repository.save(product),
                        catalogTaskExecutor
                ))
                .collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }
}
