package eshop.backend.uztupyte.model.dao;

import eshop.backend.uztupyte.api.model.VerificationToken;
import eshop.backend.uztupyte.model.Customer;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface VerificationTokenDAO extends ListCrudRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);

    void deleteByCustomer(Customer customer);
}
