package com.metis.backend.service;

import com.metis.backend.dto.ProductRequest;
import com.metis.backend.dto.ProductResponse;
import com.metis.backend.entity.Category;
import com.metis.backend.entity.Product;
import com.metis.backend.repository.CategoryRepository;
import com.metis.backend.repository.ProductRepository;
import org.springframework.stereotype.Service;
import com.metis.backend.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository) {

        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<ProductResponse> getProductById(Long id) {
        return productRepository.findById(id)
                .map(this::toResponse);
    }

    public ProductResponse createProduct(ProductRequest request) {

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found"));

        Product product = new Product(
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getStockQuantity(),
                category
        );

        Product savedProduct = productRepository.save(product);

        return toResponse(savedProduct);
    }

    public Optional<ProductResponse> updateProduct(
            Long id,
            ProductRequest request) {

        return productRepository.findById(id)
                .map(product -> {

                    Category category = categoryRepository
                            .findById(request.getCategoryId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Category not found"));

                    product.setName(request.getName());
                    product.setDescription(request.getDescription());
                    product.setPrice(request.getPrice());
                    product.setStockQuantity(request.getStockQuantity());
                    product.setCategory(category);

                    return toResponse(productRepository.save(product));
                });
    }

    public boolean deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            return false;
        }

        productRepository.deleteById(id);
        return true;
    }

    private ProductResponse toResponse(Product product) {

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getCategory().getId(),
                product.getCategory().getName()
        );
    }
}