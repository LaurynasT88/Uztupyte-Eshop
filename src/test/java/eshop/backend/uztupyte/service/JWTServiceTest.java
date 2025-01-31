package eshop.backend.uztupyte.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.MissingClaimException;
import eshop.backend.uztupyte.model.Customer;
import eshop.backend.uztupyte.model.dao.CustomerDAO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc

public class JWTServiceTest {

    @Value("${jwt.algorithm.key}")
    private String algorithmKey;

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

    @Test
    public void testJWTCorrectlySignedNoIssuer(){
        String token =
                JWT.create().withClaim("USERNAME", "UserA")
                        .sign(Algorithm.HMAC256(algorithmKey));
        Assertions.assertThrows(MissingClaimException.class, () -> jwtService.getUsername(token));
    }
}
