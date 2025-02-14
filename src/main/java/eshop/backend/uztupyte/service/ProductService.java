package eshop.backend.uztupyte.service;

import eshop.backend.uztupyte.api.model.AdminUpdateProductRequest;
import eshop.backend.uztupyte.exception.ResourceNotFoundException;
import eshop.backend.uztupyte.model.Inventory;
import eshop.backend.uztupyte.model.Product;
import eshop.backend.uztupyte.model.dao.InventoryDAO;
import eshop.backend.uztupyte.model.dao.ProductDAO;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductDAO productDAO;
    private final InventoryDAO inventoryDAO;


    @Autowired
    public ProductService(ProductDAO productDAO,InventoryDAO inventoryDAO) {
        this.productDAO = productDAO;
        this.inventoryDAO = inventoryDAO;
    }


    public Product createProduct(Product product) {
        // Save product first
        Product savedProduct = productDAO.save(product);

        // Ensure inventory is created and saved separately
        if (inventoryDAO.findByProduct(savedProduct).isEmpty()) {
            Inventory inventory = new Inventory();
            inventory.setProduct(savedProduct);
            inventory.setQuantity(0); // Default quantity to 0 if none provided

            // Save inventory explicitly
            inventory = inventoryDAO.save(inventory);

            // Link inventory back to product and save
            savedProduct.setInventory(inventory);
            savedProduct = productDAO.save(savedProduct);
        }

        return savedProduct;
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