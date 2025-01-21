package eshop.backend.uztupyte.model.dao;

import eshop.backend.uztupyte.model.Product;
import org.springframework.data.repository.ListCrudRepository;

public interface ProductDAO extends ListCrudRepository<Product, Long> {
}
