package eshop.backend.uztupyte.config;

import eshop.backend.uztupyte.model.Customer;
import eshop.backend.uztupyte.model.Inventory;
import eshop.backend.uztupyte.model.Product;
import eshop.backend.uztupyte.model.RoleName;
import eshop.backend.uztupyte.model.UserRole;
import eshop.backend.uztupyte.model.dao.CustomerDAO;
import eshop.backend.uztupyte.model.dao.InventoryDAO;
import eshop.backend.uztupyte.model.dao.ProductDAO;
import eshop.backend.uztupyte.model.dao.UserRoleDAO;
import java.util.Optional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestDataInitializer {

    private final CustomerDAO customerDAO;
    private final UserRoleDAO userRoleDAO;
    private final ProductDAO productDAO;
    private final InventoryDAO inventoryDAO;

    public TestDataInitializer(CustomerDAO customerDAO, UserRoleDAO userRoleDAO, ProductDAO productDAO,
            InventoryDAO inventoryDAO) {
        this.customerDAO = customerDAO;
        this.userRoleDAO = userRoleDAO;
        this.productDAO = productDAO;
        this.inventoryDAO = inventoryDAO;
    }

    @Bean
    public CommandLineRunner seedData() {
        return args -> insertTestData();
    }

    public void insertTestData() {
        insertCustomers();
        insertUserRoles();
        insertProducts();
    }

    private void insertCustomers() {
        createCustomer("jonas@jonas.com", "jonas", "Jonas", "K",
                "$2a$10$wi4NYVweOjDCN.jI.QbyO.ZTWZgWymsoyX.N/ogs9PU7xc9YsQayq", true);

        createCustomer("zigmas@zigmas.com", "zigmas", "Zigmas", "Z",
                "$2a$10$wi4NYVweOjDCN.jI.QbyO.E2u8YBe7GvDGm3.vtxRsQHd0vj9hHLe", true);
    }

    private void insertUserRoles() {
        createUserRole(RoleName.ADMIN, "jonas@jonas.com");
        createUserRole(RoleName.USER, "zigmas@zigmas.com");
    }

    private void insertProducts() {

        createProduct("Laptop X1", "Powerful Laptop", "A high-end laptop for professionals.", 1200.99, 10);
        createProduct("Smartphone Pro", "Flagship Smartphone", "A top-tier smartphone with cutting-edge features.",
                999.99, 20);
        createProduct("Wireless Headphones", "Noise Cancelling Headphones",
                "Premium headphones with excellent sound quality.", 249.99, 30);
    }

    private void createCustomer(String email, String username, String firstName,
            String lastName, String hashedPassword, boolean emailVerified) {

        if (customerDAO.findByEmailIgnoreCase(email).isEmpty()) {
            Customer customer = new Customer();
            customer.setUsername(username);
            customer.setEmail(email);
            customer.setFirstName(firstName);
            customer.setLastName(lastName);
            customer.setPassword(hashedPassword);
            customer.setEmailVerified(emailVerified);

            customerDAO.save(customer);
            System.out.println("Inserted customer: " + username);
        } else {
            System.out.println("Customer already exists: " + username);
        }
    }

    private void createUserRole(RoleName roleName, String email) {
        Optional<Customer> customerOpt = customerDAO.findByEmailIgnoreCase(email);
        if (customerOpt.isPresent() && userRoleDAO.findByCustomerId(customerOpt.get().getId()).isEmpty()) {
            UserRole userRole = new UserRole();
            userRole.setName(roleName);
            userRole.setCustomer(customerOpt.get());
            userRoleDAO.save(userRole);
            System.out.println("Inserted role: " + roleName + " for " + email);
        } else {
            System.out.println("Role already exists for " + email);
        }
    }

    private void createProduct(String name, String shortDescription, String longDescription, Double price,
            int quantity) {

        if (productDAO.findByName(name).isEmpty()) {
            Product product = new Product();
            product.setName(name);
            product.setShortDescription(shortDescription);
            product.setLongDescription(longDescription);
            product.setPrice(price);

            productDAO.save(product);
            System.out.println("Inserted product: " + name);

            createInventory(product, quantity);
        } else {
            System.out.println("Product already exists: " + name);
        }
    }

    private void createInventory(Product product, int quantity) {

        if (inventoryDAO.findByProduct(product).isEmpty()) {
            Inventory inventory = new Inventory();
            inventory.setProduct(product);
            inventory.setQuantity(quantity);
            inventoryDAO.save(inventory);
            System.out.println("Inserted inventory for product: " + product.getName() + " with quantity: " + quantity);
        } else {
            System.out.println("Inventory already exists for product: " + product.getName());
        }
    }
}
