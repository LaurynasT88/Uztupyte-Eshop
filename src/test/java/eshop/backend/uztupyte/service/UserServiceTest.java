package eshop.backend.uztupyte.service;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import eshop.backend.uztupyte.api.model.LoginBody;
import eshop.backend.uztupyte.api.model.PasswordResetBody;
import eshop.backend.uztupyte.api.model.RegistrationBody;
import eshop.backend.uztupyte.exception.EmailNotFoundException;
import eshop.backend.uztupyte.model.Customer;
import eshop.backend.uztupyte.model.VerificationToken;
import eshop.backend.uztupyte.exception.EmailFailureException;
import eshop.backend.uztupyte.exception.UserAlreadyExistsException;
import eshop.backend.uztupyte.exception.UserNotVerifiedException;
import eshop.backend.uztupyte.model.dao.CustomerDAO;
import eshop.backend.uztupyte.model.dao.VerificationTokenDAO;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc

public class UserServiceTest {

    @RegisterExtension
    private static GreenMailExtension greenMailExtension = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("springboot" , "secret"))
            .withPerMethodLifecycle(true);

    @Autowired
    private CustomerService customerService;
    @Autowired
    private VerificationTokenDAO  verificationTokenDAO;
    @Autowired
    private CustomerDAO customerDAO;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private EncryptionService encryptionService;

    @Test
    @Transactional
    public void testRegisterUser() throws MessagingException {
        RegistrationBody body = new RegistrationBody();
        body.setUsername("UserA");
        body.setEmail("UserServiceTest$testRegisterUser@junit.com");
        body.setFirstName("FirstName");
        body.setLastName("LastName");
        body.setPassword("MySecretPassword123");
        Assertions.assertThrows(UserAlreadyExistsException.class,
                () -> customerService.registerCustomer(body), "Username should already be in use.");
        body.setUsername("UserServiceTest$testRegisterUser");
        body.setEmail("UserA@junit.com");
        Assertions.assertThrows(UserAlreadyExistsException.class,
                () -> customerService.registerCustomer(body), "Email should already be in use.");
        body.setEmail("UserServiceTest$testRegisterUser@junit.com");
        Assertions.assertDoesNotThrow(() -> customerService.registerCustomer(body),
                "User should register successfully.");
        Assertions.assertEquals(body.getEmail(), greenMailExtension.getReceivedMessages()[0]
                .getRecipients(Message.RecipientType.TO)[0].toString());
    }

    @Test
    @Transactional
    public void testLoginUser() throws UserNotVerifiedException, EmailFailureException {
        LoginBody body = new LoginBody();
        body.setUsername("UserA-NotExists");
        body.setPassword("PasswordA123-BadPassword");
        Assertions.assertNull(customerService.loginCustomer(body), "The user should not exist.");
        body.setUsername("UserA");
        Assertions.assertNull(customerService.loginCustomer(body), "The password should be incorrect.");
        body.setPassword("PasswordA123");
        Assertions.assertNotNull(customerService.loginCustomer(body), "The user should login successfully.");
        body.setUsername("UserB");
        body.setPassword("PasswordB123");
        try {
            customerService.loginCustomer(body);
            Assertions.assertTrue(false, "User should not have email verified.");
        } catch (UserNotVerifiedException ex) {
            Assertions.assertTrue(ex.isNewEmailSent(), "Email verification should be sent.");
            Assertions.assertEquals(1, greenMailExtension.getReceivedMessages().length);
        }
        try {
            customerService.loginCustomer(body);
            Assertions.assertTrue(false, "User should not have email verified.");
        } catch (UserNotVerifiedException ex) {
            Assertions.assertFalse(ex.isNewEmailSent(), "Email verification should not be resent.");
            Assertions.assertEquals(1, greenMailExtension.getReceivedMessages().length);
        }
    }

    @Test
    @Transactional
    public void testVerifyUser() throws EmailFailureException {
        Assertions.assertFalse(customerService.verifyCustomer("Bad Token"),"Token that is bad or does not exist should return false. " );
        LoginBody body = new LoginBody();
        body.setUsername("UserB");
        body.setPassword("PasswordB123");
        try {
            customerService.loginCustomer(body);
            Assertions.assertTrue(false, "User should not have email verified.");
        } catch (UserNotVerifiedException ex) {
            List<VerificationToken> tokens = verificationTokenDAO.findByCustomer_IdOrderByIdDesc(2L);
            String token = tokens.get(0).getToken();
            Assertions.assertTrue(customerService.verifyCustomer(token), "Token should be valid");
            Assertions.assertNotNull(token, "The user should now be verified");


        }

    }

    @Test
    @Transactional
    public void testForgotPassword() throws MessagingException {
        Assertions.assertThrows(EmailNotFoundException.class,
                () -> customerService.forgotPassword("UserNotExist@junit.com"));
        Assertions.assertDoesNotThrow(() -> customerService.forgotPassword(
                "UserA@junit.com"), "Non existing email should be rejected.");
        Assertions.assertEquals("UserA@junit.com",
                greenMailExtension.getReceivedMessages()[0]
                        .getRecipients(Message.RecipientType.TO)[0].toString(), "Password " +
                        "reset email should be sent.");
    }

    public void testResetPassword() {
        Customer customer = customerDAO.findByUsernameIgnoreCase("UserA").get();
        String token = jwtService.generatePasswordResetJWT(customer);
        PasswordResetBody body = new PasswordResetBody();
        body.setToken(token);
        body.setPassword("Password123456");
        customerService.resetPassword(body);
        customer = customerDAO.findByUsernameIgnoreCase("UserA").get();
        Assertions.assertTrue(encryptionService.verifyPassword("Password123456",
                customer.getPassword()), "Password change should be written to DB.");
    }


}



