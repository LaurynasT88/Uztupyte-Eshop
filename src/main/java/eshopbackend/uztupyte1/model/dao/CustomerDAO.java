package eshopbackend.uztupyte1.model.dao;

import eshopbackend.uztupyte1.model.Customer;
import org.springframework.data.repository.CrudRepository;

public interface CustomerDAO extends CrudRepository<Customer, Long> {
}
