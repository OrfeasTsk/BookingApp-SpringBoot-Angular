package tedi.backend.repositories;

import tedi.backend.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long>,RoleRepositoryCustom {
}
