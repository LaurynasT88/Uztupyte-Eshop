package eshopbackend.uztupyte1.service;

import eshopbackend.uztupyte1.api.model.RegistrationBody;
import eshopbackend.uztupyte1.model.Customer;
import eshopbackend.uztupyte1.model.dao.CustomerDAO;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private CustomerDAO customerDAO;

    public CustomerService(CustomerDAO customerDAO) {

        this.customerDAO = customerDAO;

    }


    public Customer registerCustomer(RegistrationBody registrationBody) {
        Customer customer = new Customer();
        customer.setEmail(registrationBody.getEmail());
        customer.setFirstName(registrationBody.getFirstName());
        customer.setLastName(registrationBody.getLastName());
        customer.setUsername(registrationBody.getUsername());
        //TODO: Encrypt passwords!!
        customer.setPassword(registrationBody.getPassword());
        customer = customerDAO.save(customer);
        return customer;

    }

}
