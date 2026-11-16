package com.metis.backend.repository;

import com.metis.backend.entity.ProductSpecification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductSpecificationRepository
        extends JpaRepository<ProductSpecification, Long> {

    List<ProductSpecification> findAllByProduct_Id(Long productId);

    Optional<ProductSpecification> findByIdAndProduct_Id(
            Long id,
            Long productId
    );
}