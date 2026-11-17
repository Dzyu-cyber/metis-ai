package com.metis.backend.controller;

import com.metis.backend.dto.ProductSpecificationRequest;
import com.metis.backend.dto.ProductSpecificationResponse;
import com.metis.backend.service.ProductSpecificationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/specifications")
public class ProductSpecificationController {

    private final ProductSpecificationService specificationService;

    public ProductSpecificationController(
            ProductSpecificationService specificationService) {

        this.specificationService = specificationService;
    }

    @GetMapping
    public List<ProductSpecificationResponse> getSpecifications(
            @PathVariable Long productId) {

        return specificationService.getSpecifications(productId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ProductSpecificationResponse createSpecification(
            @PathVariable Long productId,
            @Valid @RequestBody ProductSpecificationRequest request) {

        return specificationService.createSpecification(productId, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ProductSpecificationResponse updateSpecification(
            @PathVariable Long productId,
            @PathVariable Long id,
            @Valid @RequestBody ProductSpecificationRequest request) {

        return specificationService.updateSpecification(productId, id, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpecification(
            @PathVariable Long productId,
            @PathVariable Long id) {

        specificationService.deleteSpecification(productId, id);

        return ResponseEntity.noContent().build();
    }
}