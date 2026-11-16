
package com.metis.backend.service;

import com.metis.backend.dto.ProductReviewRequest;
import com.metis.backend.dto.ProductReviewResponse;
import com.metis.backend.dto.ProductReviewSummaryResponse;
import com.metis.backend.entity.Product;
import com.metis.backend.entity.ProductReview;
import com.metis.backend.exception.ResourceNotFoundException;
import com.metis.backend.repository.ProductRepository;
import com.metis.backend.repository.ProductReviewRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductReviewService {

    private final ProductReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    public ProductReviewService(
            ProductReviewRepository reviewRepository,
            ProductRepository productRepository) {

        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
    }

    public List<ProductReviewResponse> getReviews(Long productId) {

        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found");
        }

        return reviewRepository
                .findAllByProduct_IdOrderByCreatedAtDesc(productId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductReviewSummaryResponse getReviewSummary(Long productId) {

        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found");
        }

        Double averageRating =
                reviewRepository.findAverageRatingByProductId(productId);

        long totalReviews =
                reviewRepository.countByProduct_Id(productId);

        return new ProductReviewSummaryResponse(
                averageRating == null ? 0.0 : averageRating,
                totalReviews
        );
    }

    public ProductReviewResponse createReview(
            Long productId,
            ProductReviewRequest request) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found"));

        ProductReview review = new ProductReview(
                request.getReviewerName(),
                request.getRating(),
                request.getComment(),
                product
        );

        return toResponse(reviewRepository.save(review));
    }

    @Transactional
    public ProductReviewResponse updateReview(
            Long reviewId,
            ProductReviewRequest request) {

        ProductReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Review not found"));

        review.setReviewerName(request.getReviewerName());
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        return toResponse(reviewRepository.save(review));
    }

    public void deleteReview(Long reviewId) {

        ProductReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Review not found"));

        reviewRepository.delete(review);
    }

    private ProductReviewResponse toResponse(ProductReview review) {

        return new ProductReviewResponse(
                review.getId(),
                review.getReviewerName(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}
