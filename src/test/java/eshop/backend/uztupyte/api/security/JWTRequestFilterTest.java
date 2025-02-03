package eshop.backend.uztupyte.api.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import eshop.backend.uztupyte.model.Customer;
import eshop.backend.uztupyte.model.dao.CustomerDAO;
import eshop.backend.uztupyte.service.JWTService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class JWTRequestFilterTest {


    @Autowired
    private MockMvc mvc;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private CustomerDAO customerDAO;

    private static final String AUTHENTICATED_PATH = "/auth/me";


    @Test
    public void testUnauthenticatedRequest() throws Exception {
        mvc.perform(get(AUTHENTICATED_PATH)).andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }


    @Test
    public void testBadToken() throws Exception {
        mvc.perform(get(AUTHENTICATED_PATH).header("Authorization", "BadTokenThatIsNotValid"))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
        mvc.perform(get(AUTHENTICATED_PATH).header("Authorization", "Bearer BadTokenThatIsNotValid"))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    public void testUnverifiedUser() throws Exception {
        Customer customer = customerDAO.findByUsernameIgnoreCase("UserB").get();
        String token = jwtService.generateJWT(customer);
        mvc.perform(get(AUTHENTICATED_PATH).header("Authorization", "Bearer " + token))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }


    @Test
    public void testSuccessful() throws Exception {
        Customer customer = customerDAO.findByUsernameIgnoreCase("UserA").get();
        String token = jwtService.generateJWT(customer);
        mvc.perform(get(AUTHENTICATED_PATH).header("Authorization", "Bearer " + token))
                .andExpect(status().is(HttpStatus.OK.value()));
    }

    @Test
    public void testLoginJWTNotGeneratedByMe(){
        String token =
        JWT.create().withClaim("USERNAME", "UserA").sign(Algorithm.HMAC256(
                "NotTheRealSecret"));
        Assertions.assertThrows(SignatureVerificationException.class,
                () -> jwtService.getUsername(token));



    }

}