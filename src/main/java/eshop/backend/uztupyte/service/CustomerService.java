package eshop.backend.uztupyte.service;

import eshop.backend.uztupyte.api.model.LoginBody;
import eshop.backend.uztupyte.api.model.RegistrationBody;
import eshop.backend.uztupyte.exception.UserAlreadyExistsException;
import eshop.backend.uztupyte.model.Customer;
import eshop.backend.uztupyte.model.dao.CustomerDAO;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class CustomerService {

    private CustomerDAO customerDAO;
    private EncryptionService encryptionService;
    private JWTService jwtService;

    public CustomerService(CustomerDAO customerDAO, EncryptionService encryptionService, JWTService jwtService) {

        this.customerDAO = customerDAO;
        this.encryptionService = encryptionService;
        this.jwtService = jwtService;
    }


    public Customer registerCustomer(RegistrationBody registrationBody) throws UserAlreadyExistsException {

        if (customerDAO.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent()
                || customerDAO.findByUsernameIgnoreCase(registrationBody.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException();
        }

        Customer customer = new Customer();
        customer.setEmail(registrationBody.getEmail());
        customer.setFirstName(registrationBody.getFirstName());
        customer.setLastName(registrationBody.getLastName());
        customer.setUsername(registrationBody.getUsername());
        customer.setPassword(encryptionService.encryptPassword(registrationBody.getPassword()));
        return customerDAO.save(customer);

    }

    public String loginCustomer(LoginBody loginBody)  {
        Optional<Customer> opCustomer = customerDAO.findByUsernameIgnoreCase(loginBody.getUsername());
        if(opCustomer.isPresent()) {
            Customer customer = opCustomer.get();
            if(encryptionService.verifyPassword(loginBody.getPassword(), customer.getPassword())) {
                return jwtService.generateJWT(customer);
            }
        }
        return null;
    }

}
