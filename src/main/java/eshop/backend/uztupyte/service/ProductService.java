package eshop.backend.uztupyte.service;

import eshop.backend.uztupyte.api.model.AdminUpdateProductRequest;
import eshop.backend.uztupyte.exception.ResourceNotFoundException;
import eshop.backend.uztupyte.model.Inventory;
import eshop.backend.uztupyte.model.Product;
import eshop.backend.uztupyte.model.dao.InventoryDAO;
import eshop.backend.uztupyte.model.dao.ProductDAO;
import eshop.backend.uztupyte.util.Loggable;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService implements Loggable {

    private final ProductDAO productDAO;
    private final InventoryDAO inventoryDAO;


    @Autowired
    public ProductService(ProductDAO productDAO, InventoryDAO inventoryDAO) {
        this.productDAO = productDAO;
        this.inventoryDAO = inventoryDAO;
    }

    public Product createProduct(Product product) {

        Product savedProduct = productDAO.save(product);

        if (inventoryDAO.findByProduct(savedProduct).isEmpty()) {

            Inventory inventory = new Inventory();
            inventory.setProduct(savedProduct);
            inventory.setQuantity(0);
            inventory = inventoryDAO.save(inventory);

            savedProduct.setInventory(inventory);
            savedProduct = productDAO.save(savedProduct);
        }

        return savedProduct;
    }

    public List<Product> getAllProducts() {
        return productDAO.findAll();
    }

    public Product updateProduct(Long id, AdminUpdateProductRequest request) {

        Product existingProduct = getProductByIdOrThrow(id);
        existingProduct.setName(request.getName());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setShortDescription(request.getShortDescription());
        existingProduct.setLongDescription(request.getLongDescription());

        Inventory inventory = existingProduct.getInventory();
        inventory.setQuantity(request.getQuantity());

        Product updated = productDAO.save(existingProduct);
        getLogger().info("Updated product [{}]", id);
        return updated;
    }

    public void deleteProduct(Long id) {

        Product product = getProductByIdOrThrow(id);
        productDAO.delete(product);
        getLogger().info("Deleted product [{}]", id);
    }

    public Product getProductByIdOrThrow(Long id) {

        return productDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }
}