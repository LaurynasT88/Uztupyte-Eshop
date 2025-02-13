package eshop.backend.uztupyte.model.dao;

import eshop.backend.uztupyte.model.UserRole;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleDAO extends JpaRepository<UserRole, Long> {
    Optional<UserRole> findByCustomerId(Long customerId);
}
