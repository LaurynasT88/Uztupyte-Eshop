package eshop.backend.uztupyte.model.dao;

import eshop.backend.uztupyte.model.Inventory;
import eshop.backend.uztupyte.model.Product;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface InventoryDAO extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProduct(Product product);
}
