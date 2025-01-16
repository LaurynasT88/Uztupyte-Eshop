package eshopbackend.uztupyte1.model.dao;

import eshopbackend.uztupyte1.model.Customer;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CustomerDAO extends CrudRepository<Customer, Long> {


    Optional<Customer> findByUsernameIgnoreCase(String username);

    Optional<Customer> findByEmailIgnoreCase(String email);
}
