package eshop.backend.uztupyte.api.security;

import eshop.backend.uztupyte.model.Customer;
import eshop.backend.uztupyte.model.dao.CustomerDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@Primary
public class JUnitUserDetailsService implements UserDetailsService {


    @Autowired
    private CustomerDAO customerDAO;


    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Customer> opUser = customerDAO.findByUsernameIgnoreCase(username);
        if (opUser.isPresent())
            return opUser.get();
        return null;
    }

}