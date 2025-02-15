package eshop.backend.uztupyte.model.dao;

import eshop.backend.uztupyte.model.Product;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDAO extends JpaRepository<Product, Long> {

    Optional<Product> findByName(String name);
}
