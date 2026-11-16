
package com.metis.backend.controller;

import com.metis.backend.dto.ProductReviewRequest;
import com.metis.backend.dto.ProductReviewResponse;
import com.metis.backend.service.ProductReviewService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ProductReviewService reviewService;

    public ReviewController(ProductReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PutMapping("/{reviewId}")
    public ProductReviewResponse updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ProductReviewRequest request) {

        return reviewService.updateReview(reviewId, request);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long reviewId) {

        reviewService.deleteReview(reviewId);

        return ResponseEntity.noContent().build();
    }
}
