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
import eshop.backend.uztupyte.util.Loggable;
import java.util.Optional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestDataInitializer implements Loggable {

    private static final String TEST_ADMIN_USERNAME = "jonas";
    private static final String TEST_ADMIN_EMAIL = "jonas@jonas.com";
    //decrypted: jonaS1
    private static final String TEST_ADMIN_PASSWORD = "$2a$10$wi4NYVweOjDCN.jI.QbyO.ZTWZgWymsoyX.N/ogs9PU7xc9YsQayq";

    private static final String TEST_USER_USERNAME = "zigmas";
    private static final String TEST_USER_EMAIL = "zigmas@zigmas.com";
    //decrypted: zigmaS1
    private static final String TEST_USER_PASSWORD = "$2a$10$wi4NYVweOjDCN.jI.QbyO.E2u8YBe7GvDGm3.vtxRsQHd0vj9hHLe";

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
    public CommandLineRunner seedTestData() {
        return args -> insertTestData();
    }

    public void insertTestData() {
        insertCustomers();
        insertUserRoles();
        insertProducts();
    }

    private void insertCustomers() {

        createCustomer(TEST_ADMIN_EMAIL, TEST_ADMIN_USERNAME, "Jonas", "K",
                TEST_ADMIN_PASSWORD);

        createCustomer(TEST_USER_EMAIL, TEST_USER_USERNAME, "Zigmas", "Z",
                TEST_USER_PASSWORD);
    }

    private void insertUserRoles() {

        createUserRole(RoleName.ADMIN, TEST_ADMIN_EMAIL);
        createUserRole(RoleName.USER, TEST_USER_EMAIL);
    }

    private void insertProducts() {

        createProduct("Dream Catcher",
                "Handmade from recycled materials",
                "Handmade dream catcher, home decor, wall decor, bedroom decor, Witchy artwork, Boho style, Spiritual atmosphere, Zero waste, Sustainable art",
                24.59,
                1);

        createProduct("Woman Painting",
                "Painting of the woman",
                "ORIGINAL Women hand-painted acrylic art, Bedroom wall decor, Relaxing half nude female back, Moody portrait painting, Feminine power, Relax",
                131.13,
                1);

        createProduct("Collage 'War Art' ",
                "PRINTABLE Collage",
                "PRINTABLE Collage, War art, Hand-cut artwork, Sustainable magazine scrap print, Digital clipping poster, European artist works, Politic top.",
                1.65,
                1);
    }

    private void createCustomer(String email, String username, String firstName,
            String lastName, String hashedPassword) {

        if (customerDAO.findByEmailIgnoreCase(email).isEmpty()) {
            Customer customer = new Customer();
            customer.setUsername(username);
            customer.setEmail(email);
            customer.setFirstName(firstName);
            customer.setLastName(lastName);
            customer.setPassword(hashedPassword);
            customer.setEmailVerified(true);
            customerDAO.save(customer);

            getLogger().info("Created new user [{}]", username);
        }
    }

    private void createUserRole(RoleName roleName, String email) {

        Optional<Customer> customerOpt = customerDAO.findByEmailIgnoreCase(email);

        if (customerOpt.isPresent() && userRoleDAO.findByCustomerId(customerOpt.get().getId()).isEmpty()) {
            UserRole userRole = new UserRole();
            userRole.setName(roleName);
            userRole.setCustomer(customerOpt.get());
            userRoleDAO.save(userRole);

            getLogger().info("Created role [{}], for user [{}]", roleName, customerOpt.get().getUsername());
        }
    }

    private void createProduct(String name, String shortDescription, String longDescription, Double price, int quantity) {

        if (productDAO.findByName(name).isEmpty()) {
            Product product = new Product();
            product.setName(name);
            product.setShortDescription(shortDescription);
            product.setLongDescription(longDescription);
            product.setPrice(price);
            product = productDAO.save(product);

            Inventory inventory = new Inventory();
            inventory.setProduct(product);
            inventory.setQuantity(quantity);
            inventory = inventoryDAO.save(inventory);

            product.setInventory(inventory);
            productDAO.save(product);

            getLogger().info("Created product [{}]", name);
        }
    }
}
