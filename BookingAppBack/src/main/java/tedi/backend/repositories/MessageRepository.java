package tedi.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tedi.backend.model.Message;

public interface MessageRepository extends JpaRepository<Message, Long>, MessageRepositoryCustom {
}
