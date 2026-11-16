package com.metis.backend.service;

import com.metis.backend.dto.ProductSpecificationRequest;
import com.metis.backend.dto.ProductSpecificationResponse;
import com.metis.backend.entity.Product;
import com.metis.backend.entity.ProductSpecification;
import com.metis.backend.exception.ResourceNotFoundException;
import com.metis.backend.repository.ProductRepository;
import com.metis.backend.repository.ProductSpecificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductSpecificationService {

    private final ProductSpecificationRepository specificationRepository;
    private final ProductRepository productRepository;

    public ProductSpecificationService(
            ProductSpecificationRepository specificationRepository,
            ProductRepository productRepository) {

        this.specificationRepository = specificationRepository;
        this.productRepository = productRepository;
    }

    public List<ProductSpecificationResponse> getSpecifications(Long productId) {

        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found");
        }

        return specificationRepository.findAllByProduct_Id(productId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductSpecificationResponse createSpecification(
            Long productId,
            ProductSpecificationRequest request) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found"));

        ProductSpecification specification = new ProductSpecification(
                request.getSpecName(),
                request.getSpecValue(),
                product
        );

        return toResponse(specificationRepository.save(specification));
    }

    public ProductSpecificationResponse updateSpecification(
            Long productId,
            Long id,
            ProductSpecificationRequest request) {

        ProductSpecification specification = specificationRepository
                .findByIdAndProduct_Id(id, productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Specification not found"));

        specification.setSpecName(request.getSpecName());
        specification.setSpecValue(request.getSpecValue());

        return toResponse(specificationRepository.save(specification));
    }

    public void deleteSpecification(Long productId, Long id) {

        ProductSpecification specification = specificationRepository
                .findByIdAndProduct_Id(id, productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Specification not found"));

        specificationRepository.delete(specification);
    }

    private ProductSpecificationResponse toResponse(
            ProductSpecification specification) {

        return new ProductSpecificationResponse(
                specification.getId(),
                specification.getSpecName(),
                specification.getSpecValue()
        );
    }
}