package eshop.backend.uztupyte.model.dao;

import eshop.backend.uztupyte.model.Customer;
import eshop.backend.uztupyte.model.VerificationToken;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface VerificationTokenDAO extends ListCrudRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);

    void deleteByCustomer(Customer customer);

    List<VerificationToken> findByCustomer_IdOrderByIdDesc(Long id);
}
