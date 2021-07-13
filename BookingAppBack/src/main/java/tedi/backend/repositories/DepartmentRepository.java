package tedi.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tedi.backend.model.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long>, DepartmentRepositoryCustom {
}
