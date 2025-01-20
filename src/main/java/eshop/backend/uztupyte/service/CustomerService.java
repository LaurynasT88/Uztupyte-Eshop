package eshop.backend.uztupyte.service;

import eshop.backend.uztupyte.api.model.RegistrationBody;
import eshop.backend.uztupyte.exception.UserAlreadyExistsException;
import eshop.backend.uztupyte.model.Customer;
import eshop.backend.uztupyte.model.dao.CustomerDAO;
import org.springframework.stereotype.Service;


@Service
public class CustomerService {

    private CustomerDAO customerDAO;
    private EncryptionService encryptionService;


    public CustomerService(CustomerDAO customerDAO, EncryptionService encryptionService) {

        this.customerDAO = customerDAO;
        this.encryptionService = encryptionService;
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

}
