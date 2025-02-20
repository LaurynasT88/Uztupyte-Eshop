package eshop.backend.uztupyte.api.controller.product;

import eshop.backend.uztupyte.model.Product;
import eshop.backend.uztupyte.service.ProductImageService;
import eshop.backend.uztupyte.service.ProductService;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductByIdOrThrow(id);
        return ResponseEntity.ok(product);
    }


    @GetMapping(
            value = "/{productId}/images",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public byte[] getProductImage(@PathVariable("productId") Long productId) {

        return productImageService.getProductImage(productId);
    }
}