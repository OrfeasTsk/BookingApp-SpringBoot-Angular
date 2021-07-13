package tedi.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tedi.backend.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {
}
