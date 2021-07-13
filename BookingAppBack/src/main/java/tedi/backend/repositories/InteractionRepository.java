package tedi.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tedi.backend.model.Interaction;

public interface InteractionRepository extends JpaRepository<Interaction, Long>, InteractionRepositoryCustom{
}
