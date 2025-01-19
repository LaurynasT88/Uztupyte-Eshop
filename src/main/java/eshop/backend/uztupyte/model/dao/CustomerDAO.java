package eshop.backend.uztupyte.model.dao;

import eshop.backend.uztupyte.model.Customer;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CustomerDAO extends CrudRepository<Customer, Long> {


    Optional<Customer> findByUsernameIgnoreCase(String username);

    Optional<Customer> findByEmailIgnoreCase(String email);
}
