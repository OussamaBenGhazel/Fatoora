package tn.tradenet.elfatoora.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.tradenet.elfatoora.domain.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}

