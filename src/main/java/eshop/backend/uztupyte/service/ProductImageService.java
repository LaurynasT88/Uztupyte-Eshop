package eshop.backend.uztupyte.service;

import eshop.backend.uztupyte.config.S3Buckets;
import eshop.backend.uztupyte.exception.ResourceNotFoundException;
import eshop.backend.uztupyte.model.Product;
import eshop.backend.uztupyte.model.ProductImage;
import eshop.backend.uztupyte.model.dao.ProductDAO;
import eshop.backend.uztupyte.model.dao.ProductImageDAO;
import eshop.backend.uztupyte.util.Loggable;
import java.io.IOException;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
