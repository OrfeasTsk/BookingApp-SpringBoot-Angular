package tedi.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tedi.backend.model.Photo;

public interface PhotoRepository extends JpaRepository<Photo, Long>, PhotoRepositoryCustom {
}
