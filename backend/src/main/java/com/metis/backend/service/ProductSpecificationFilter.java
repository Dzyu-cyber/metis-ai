package com.metis.backend.service;

import com.metis.backend.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecificationFilter {

    public static Specification<Product> search(String keyword) {
        return (root, query, criteriaBuilder) -> {

            if (keyword == null || keyword.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            String pattern = "%" + keyword.toLowerCase() + "%";

            return criteriaBuilder.or(
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("name")),
                            pattern
                    ),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("description")),
                            pattern
                    )
            );
        };
    }

    public static Specification<Product> hasCategory(Long categoryId) {
        return (root, query, criteriaBuilder) -> {

            if (categoryId == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root.get("category").get("id"),
                    categoryId
            );
        };
    }

    public static Specification<Product> hasPriceBetween(
            BigDecimal minPrice,
            BigDecimal maxPrice) {

        return (root, query, criteriaBuilder) -> {

            if (minPrice == null && maxPrice == null) {
                return criteriaBuilder.conjunction();
            }

            if (minPrice == null) {
                return criteriaBuilder.lessThanOrEqualTo(
                        root.get("price"), maxPrice
                );
            }

            if (maxPrice == null) {
                return criteriaBuilder.greaterThanOrEqualTo(
                        root.get("price"), minPrice
                );
            }

            return criteriaBuilder.between(
                    root.get("price"),
                    minPrice,
                    maxPrice
            );
        };
    }
}