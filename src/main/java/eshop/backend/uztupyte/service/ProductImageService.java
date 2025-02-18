package eshop.backend.uztupyte.service;

import eshop.backend.uztupyte.exception.ResourceNotFoundException;
import eshop.backend.uztupyte.model.Product;
import eshop.backend.uztupyte.model.ProductImage;
import eshop.backend.uztupyte.model.dao.ProductDAO;
import eshop.backend.uztupyte.model.dao.ProductImageDAO;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductImageService {

    private final ProductImageDAO productImageDAO;
    private final ProductDAO productDAO;

    public ProductImageService(ProductImageDAO productImageDAO, ProductDAO productDAO) {
        this.productImageDAO = productImageDAO;
        this.productDAO = productDAO;
    }

    @Transactional
    public ProductImage addImageToProduct(Long productId, MultipartFile file) throws ResourceNotFoundException, IOException {
        Product product = productDAO.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        byte[] imageBytes = file.getBytes();

        ProductImage image = new ProductImage();
        //image.setImageData(imageBytes);
        image.setProduct(product);
        return productImageDAO.save(image);
    }

    @Transactional
    public void deleteImage(Long imageId) {
        productImageDAO.deleteById(imageId);
    }

    public List<ProductImage> getImagesForProduct(Long productId) {
        return productImageDAO.findByProductId(productId);
    }

    public byte[] getImageById(Long imageId) throws ResourceNotFoundException {
        ProductImage image = productImageDAO.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found"));
        return null;
    }

    public byte[] getProductImage(Long productId) {
        return null;
    }

    public void uploadProductImage(Long productId, MultipartFile file) {

    }
}
