package com.metis.backend.service;

import com.metis.backend.dto.ProductRequest;
import com.metis.backend.dto.ProductResponse;
import com.metis.backend.dto.ProductPageResponse;
import com.metis.backend.entity.Category;
import com.metis.backend.entity.Product;
import com.metis.backend.exception.ResourceNotFoundException;
import com.metis.backend.repository.CategoryRepository;
import com.metis.backend.repository.ProductRepository;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
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

    public ProductPageResponse searchProducts(
            String keyword,
            Long categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            int page,
            int size,
            String sortBy,
            String direction) {

        if (minPrice != null && maxPrice != null
                && minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException(
                    "Minimum price cannot exceed maximum price"
            );
        }

        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Invalid pagination parameters"
            );
        }

        if (!List.of("id", "name", "price").contains(sortBy)) {
            throw new IllegalArgumentException("Invalid sort field");
        }

        Sort.Direction sortDirection =
                Sort.Direction.fromOptionalString(direction)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Direction must be asc or desc"
                                ));

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, sortBy)
        );

        Specification<Product> specification =
                ProductSpecificationFilter.search(keyword)
                        .and(ProductSpecificationFilter.hasCategory(categoryId))
                        .and(ProductSpecificationFilter.hasPriceBetween(
                                minPrice, maxPrice
                        ));

        Page<Product> productPage =
                productRepository.findAll(specification, pageable);

        List<ProductResponse> products = productPage.getContent()
                .stream()
                .map(this::toResponse)
                .toList();

        return new ProductPageResponse(
                products,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast()
        );
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
