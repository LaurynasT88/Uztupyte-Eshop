package eshop.backend.uztupyte.api.controller.product;

import eshop.backend.uztupyte.exception.ResourceNotFoundException;
import eshop.backend.uztupyte.model.Product;
import eshop.backend.uztupyte.model.ProductImage;
import eshop.backend.uztupyte.service.ProductImageService;
import eshop.backend.uztupyte.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final ProductImageService productImageService;

    public ProductController(ProductService productService, ProductImageService productImageService) {
        this.productService = productService;
        this.productImageService = productImageService;
    }


    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) throws ResourceNotFoundException {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }


    @PostMapping ("/{productId}/images")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductImage> addImageToProduct(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file) throws ResourceNotFoundException, IOException {

        ProductImage productImage = productImageService.addImageToProduct(productId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(productImage);
    }



    @DeleteMapping("/images/{imageId}")
    @PreAuthorize("hasRole('ADMIN')") // Restrict to admin users
    public ResponseEntity<Void> deleteImage(@PathVariable Long imageId) {
        productImageService.deleteImage(imageId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{productId}/images")
    public ResponseEntity<List<ProductImage>> getImagesForProduct(@PathVariable Long productId) {
        List<ProductImage> images = productImageService.getImagesForProduct(productId);
        return ResponseEntity.ok(images);
    }
}