package com.project.backend.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.project.backend.models.Product;
import com.project.backend.repositories.CategoryRepository;
import com.project.backend.repositories.ProductRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:5173") 
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Vetëm Admini shton produkte
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            categoryRepository.findById(product.getCategory().getId())
                .ifPresent(product::setCategory);
        }
        return ResponseEntity.ok(productRepository.save(product));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Vetëm Admini mund të editojë
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @Valid @RequestBody Product details) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produkti nuk u gjet!"));

        product.setName(details.getName());
        product.setPrice(details.getPrice());
        product.setDescription(details.getDescription());
        
        if (details.getCategory() != null && details.getCategory().getId() != null) {
            categoryRepository.findById(details.getCategory().getId())
                .ifPresent(product::setCategory);
        }

        return ResponseEntity.ok(productRepository.save(product));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Vetëm Admini mund të fshijë
    public ResponseEntity<?> delete(@PathVariable Long id) {
        productRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}