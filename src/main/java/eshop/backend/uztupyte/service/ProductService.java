package eshop.backend.uztupyte.service;

import eshop.backend.uztupyte.api.model.AdminUpdateProductRequest;
import eshop.backend.uztupyte.exception.ResourceNotFoundException;
import eshop.backend.uztupyte.model.Inventory;
import eshop.backend.uztupyte.model.Product;
import eshop.backend.uztupyte.model.dao.ProductDAO;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductDAO productDAO;

    public ProductService(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }


    public Product createProduct(Product product) {
        return productDAO.save(product);
    }


    public List<Product> getAllProducts() {
        return productDAO.findAll();
    }


    public Product getProductById(Long id) throws ResourceNotFoundException {
        System.out.println("Fetching product with ID: " + id);
        return productDAO.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }


    public Product updateProduct(Long id, AdminUpdateProductRequest request) throws ResourceNotFoundException {

        Product existingProduct = getProductById(id);

        existingProduct.setName(request.getName());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setShortDescription(request.getShortDescription());
        existingProduct.setLongDescription(request.getLongDescription());

        Inventory inventory = existingProduct.getInventory();
        inventory.setQuantity(request.getQuantity());

        return productDAO.save(existingProduct);
    }


    public void deleteProduct(Long id) throws ResourceNotFoundException {
        Product product = getProductById(id);
        productDAO.delete(product);
    }
}