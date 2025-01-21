package eshop.backend.uztupyte.model.dao;

import eshop.backend.uztupyte.model.Customer;
import eshop.backend.uztupyte.model.WebOrder;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface WebOrderDAO extends ListCrudRepository<WebOrder, Long> {

    List<WebOrder> findByCustomer(Customer customer);
}
