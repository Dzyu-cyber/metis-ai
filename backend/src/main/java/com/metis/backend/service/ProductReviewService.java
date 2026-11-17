package com.metis.backend.service;

import com.metis.backend.dto.ProductReviewRequest;
import com.metis.backend.dto.ProductReviewResponse;
import com.metis.backend.dto.ProductReviewSummaryResponse;

import com.metis.backend.entity.AppUser;
import com.metis.backend.entity.Product;
import com.metis.backend.entity.ProductReview;

import com.metis.backend.exception.ResourceNotFoundException;

import com.metis.backend.repository.ProductRepository;
import com.metis.backend.repository.ProductReviewRepository;
import com.metis.backend.repository.UserRepository;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductReviewService {

    private final ProductReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ProductReviewService(
            ProductReviewRepository reviewRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {

        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
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

    @Transactional
    public ProductReviewResponse createReview(
            Long productId,
            ProductReviewRequest request,
            Authentication authentication) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found"));

        AppUser user = getAuthenticatedUser(authentication);

        ProductReview review = new ProductReview(
                user.getFullName(),
                request.getRating(),
                request.getComment(),
                product
        );

        review.setUser(user);

        return toResponse(reviewRepository.save(review));
    }

    @Transactional
    public ProductReviewResponse updateReview(
            Long reviewId,
            ProductReviewRequest request,
            Authentication authentication) {

        ProductReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Review not found"));

        AppUser user = getAuthenticatedUser(authentication);

        verifyOwnership(review, user, authentication);

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        return toResponse(reviewRepository.save(review));
    }

    @Transactional
    public void deleteReview(
            Long reviewId,
            Authentication authentication) {

        ProductReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Review not found"));

        AppUser user = getAuthenticatedUser(authentication);

        verifyOwnership(review, user, authentication);

        reviewRepository.delete(review);
    }

    private AppUser getAuthenticatedUser(Authentication authentication) {

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));
    }

    private void verifyOwnership(
            ProductReview review,
            AppUser user,
            Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        boolean isOwner = review.getUser() != null
                && review.getUser().getId().equals(user.getId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException(
                    "You can only modify your own reviews"
            );
        }
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
