package eshop.backend.uztupyte.model.dao;

import eshop.backend.uztupyte.model.Customer;

import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface CustomerDAO extends ListCrudRepository<Customer, Long> {


    Optional<Customer> findByUsernameIgnoreCase(String username);

    Optional<Customer> findByEmailIgnoreCase(String email);
}
