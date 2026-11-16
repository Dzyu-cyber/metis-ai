
package com.metis.backend.controller;

import com.metis.backend.dto.ProductReviewRequest;
import com.metis.backend.dto.ProductReviewResponse;
import com.metis.backend.dto.ProductReviewSummaryResponse;
import com.metis.backend.service.ProductReviewService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/reviews")
public class ProductReviewController {

    private final ProductReviewService reviewService;

    public ProductReviewController(ProductReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public List<ProductReviewResponse> getReviews(
            @PathVariable Long productId) {

        return reviewService.getReviews(productId);
    }

    @GetMapping("/summary")
    public ProductReviewSummaryResponse getReviewSummary(
            @PathVariable Long productId) {

        return reviewService.getReviewSummary(productId);
    }

    @PostMapping
    public ProductReviewResponse createReview(
            @PathVariable Long productId,
            @Valid @RequestBody ProductReviewRequest request) {

        return reviewService.createReview(productId, request);
    }
}
