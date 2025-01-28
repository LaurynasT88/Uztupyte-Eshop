package eshop.backend.uztupyte.service;

import eshop.backend.uztupyte.model.Customer;
import eshop.backend.uztupyte.model.dao.CustomerDAO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JWTServiceTest {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private CustomerDAO customerDAO;

    @Test
    public void testVerificationTokenNotUsableForLogin() {
        Customer customer = customerDAO.findByUsernameIgnoreCase("UserA").get();
        String token = jwtService.generateVerificationJWT(customer);
        Assertions.assertNull(jwtService.getUsername(token), "Verification token should not contain username. ");



    }
    @Test
    public void testAuthTokenReturnsUsername(){
        Customer customer = customerDAO.findByUsernameIgnoreCase("UserA").get();
        String token = jwtService.generateJWT(customer);
        Assertions.assertEquals(customer.getUsername(), jwtService.getUsername(token), "Token for auth should contain username. ");


    }
}
