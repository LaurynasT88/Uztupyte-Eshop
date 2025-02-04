package eshop.backend.uztupyte.model.dao;

import eshop.backend.uztupyte.model.Address;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface AddressDAO extends ListCrudRepository<Address, Long> {
    List<Address> findByCustomer_Id(Long id);


}
