package eshop.backend.uztupyte.model.dao;

import eshop.backend.uztupyte.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageDAO extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProductId(Long productId);
}
