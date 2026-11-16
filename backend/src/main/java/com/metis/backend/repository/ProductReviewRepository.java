
package com.metis.backend.repository;

import com.metis.backend.entity.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductReviewRepository
        extends JpaRepository<ProductReview, Long> {

    List<ProductReview> findAllByProduct_IdOrderByCreatedAtDesc(
            Long productId
    );

    @Query("""
            SELECT AVG(r.rating)
            FROM ProductReview r
            WHERE r.product.id = :productId
            """)
    Double findAverageRatingByProductId(
            @Param("productId") Long productId
    );

    long countByProduct_Id(Long productId);
}
