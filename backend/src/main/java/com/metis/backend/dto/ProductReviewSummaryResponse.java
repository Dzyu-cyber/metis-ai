
package com.metis.backend.dto;

public class ProductReviewSummaryResponse {

    private Double averageRating;
    private Long totalReviews;

    public ProductReviewSummaryResponse(
            Double averageRating,
            Long totalReviews) {

        this.averageRating = averageRating;
        this.totalReviews = totalReviews;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public Long getTotalReviews() {
        return totalReviews;
    }
}
