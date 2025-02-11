package eshop.backend.uztupyte.service;

import eshop.backend.uztupyte.exception.ResourceNotFoundException;
import eshop.backend.uztupyte.model.Product;
import eshop.backend.uztupyte.model.dao.ProductDAO;
import org.springframework.stereotype.Service;

import java.util.List;

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


    public Product updateProduct(Long id, Product productDetails) throws ResourceNotFoundException {
        Product existingProduct = getProductById(id);
        existingProduct.setName(productDetails.getName());
        existingProduct.setPrice(productDetails.getPrice());
        existingProduct.setShortDescription(productDetails.getShortDescription());
        existingProduct.setLongDescription(productDetails.getLongDescription());
        return productDAO.save(existingProduct);
    }


    public void deleteProduct(Long id) throws ResourceNotFoundException {
        Product product = getProductById(id);
        productDAO.delete(product);
    }
}