package eshop.backend.uztupyte.service;

import eshop.backend.uztupyte.config.S3Buckets;
import eshop.backend.uztupyte.exception.ResourceNotFoundException;
import eshop.backend.uztupyte.model.Product;
import eshop.backend.uztupyte.model.ProductImage;
import eshop.backend.uztupyte.model.dao.ProductDAO;
import eshop.backend.uztupyte.model.dao.ProductImageDAO;
import eshop.backend.uztupyte.util.Loggable;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class ProductImageService implements Loggable {

    private final ProductImageDAO productImageDAO;
    private final ProductDAO productDAO;
    private final S3Service s3Service;
    private final S3Buckets s3Buckets;


    public ProductImageService(ProductImageDAO productImageDAO, ProductDAO productDAO, S3Service s3Service, S3Buckets s3Buckets) {
        this.productImageDAO = productImageDAO;
        this.productDAO = productDAO;
        this.s3Service = s3Service;
        this.s3Buckets = s3Buckets;
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

        Product product = productDAO.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product [%s] not found.".formatted(productId)));

        if (product.getImages().isEmpty()) {
            return new byte[0];
        }

        String firstImageId = product.getImages().getFirst().getProductImageId();

        return s3Service.getObject(s3Buckets.getProduct(),
                "product-images/%s/%s".formatted(product.getId(), firstImageId));
    }

    public void uploadProductImage(Long productId, MultipartFile file) {
        Product product = productDAO.findById(productId).orElseThrow(
                () -> new ResourceNotFoundException("Product with id [%s] not found".formatted(productId)));

        String productImageId = UUID.randomUUID().toString();
        try {
            s3Service.putObject(
                    s3Buckets.getProduct(),
                    "product-images/%s/%s".formatted(product.getId(), productImageId),
                    file.getBytes()
            );
        } catch (IOException e) {
            throw new RuntimeException("failed to upload profile image", e);
        }

        ProductImage productImage = new ProductImage();
        productImage.setProductImageId(productImageId);
        productImage.setProduct(product);
        productImageDAO.save(productImage);
        getLogger().info("Product [{}] image [{}] uploaded successfully.", product.getId(), productImageId);

    }
}
