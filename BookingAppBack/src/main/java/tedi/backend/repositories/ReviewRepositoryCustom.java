package tedi.backend.repositories;

import tedi.backend.model.Review;

import java.util.List;

public interface ReviewRepositoryCustom {
    List<Review> getDepartmentReviews();
    List<Review> getHostReviews();
    Boolean hasReviewedDep(Long depId, Long userId);
    Boolean hasReviewedHost(Long tenantId, Long hostId);
    Integer getRating(Long userId,Long depId);
}
