package eshop.backend.uztupyte.service;

import eshop.backend.uztupyte.api.model.LoginBody;
import eshop.backend.uztupyte.api.model.PasswordResetBody;
import eshop.backend.uztupyte.api.model.RegistrationBody;
import eshop.backend.uztupyte.exception.EmailFailureException;
import eshop.backend.uztupyte.exception.EmailNotFoundException;
import eshop.backend.uztupyte.exception.UserAlreadyExistsException;
import eshop.backend.uztupyte.exception.UserEmailNotVerifiedException;
import eshop.backend.uztupyte.exception.UserNotFound;
import eshop.backend.uztupyte.exception.UserPasswordNotVerifiedException;
import eshop.backend.uztupyte.model.Customer;
import eshop.backend.uztupyte.model.UserRole;
import eshop.backend.uztupyte.model.VerificationToken;
import eshop.backend.uztupyte.model.dao.CustomerDAO;
import eshop.backend.uztupyte.model.dao.VerificationTokenDAO;
import eshop.backend.uztupyte.util.Loggable;
import jakarta.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;


@Service
public class CustomerService implements Loggable {

    private final CustomerDAO customerDAO;
    private final VerificationTokenDAO verificationTokenDAO;
    private final EncryptionService encryptionService;
    private final JWTService jwtService;
    private final EmailService emailService;
    private final RegistrationProperties registrationProperties;


    public CustomerService(CustomerDAO customerDAO,
            EncryptionService encryptionService,
            JWTService jwtService,
            EmailService emailService,
            VerificationTokenDAO verificationTokenDAO,
            RegistrationProperties registrationProperties) {

        this.customerDAO = customerDAO;
        this.encryptionService = encryptionService;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.verificationTokenDAO = verificationTokenDAO;
        this.registrationProperties = registrationProperties;
    }


    public Customer registerCustomer(RegistrationBody registrationBody) throws EmailFailureException {

        if (customerDAO.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent()
                || customerDAO.findByUsernameIgnoreCase(registrationBody.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("User [%s] already exists".formatted(registrationBody.getUsername()));
        }
        Customer customer = new Customer();
        customer.setEmail(registrationBody.getEmail());
        customer.setFirstName(registrationBody.getFirstName());
        customer.setLastName(registrationBody.getLastName());
        customer.setUsername(registrationBody.getUsername());
        customer.setPassword(encryptionService.encryptPassword(registrationBody.getPassword()));

        if (registrationProperties.isAutoVerificationEnabled()) {
            getLogger().info("Auto-verification enabled. User [{}] auto-verified.", registrationBody.getUsername());
            customer.setEmailVerified(true);
        } else {
            VerificationToken verificationToken = createVerificationToken(customer);
            emailService.sendVerificationEmail(verificationToken);
            getLogger().info("Verification email sent to user [{}].", registrationBody.getUsername());
        }

        Customer savedCustomer = customerDAO.save(customer);
        UserRole userRole = new UserRole();
        userRole.setName(registrationBody.getRole());
        userRole.setCustomer(savedCustomer);
        savedCustomer.getRoles().add(userRole);

        return customerDAO.save(savedCustomer);
    }

    public String loginCustomer(LoginBody loginBody) {

        Customer customer = customerDAO.findByUsernameIgnoreCase(loginBody.getUsername())
                .orElseThrow(() -> new UserNotFound(loginBody.getUsername()));

        verifyPassword(loginBody.getPassword(), customer);
        verifyEmail(customer);
        return jwtService.generateJWT(customer);
    }

    @Transactional
    public boolean verifyCustomer(String token) {
        Optional<VerificationToken> opToken = verificationTokenDAO.findByToken(token);
        if (opToken.isPresent()) {
            VerificationToken verificationToken = opToken.get();
            Customer customer = verificationToken.getCustomer();
            if (!customer.isEmailVerified()) {
                customer.setEmailVerified(true);
                customerDAO.save(customer);
                verificationTokenDAO.deleteByCustomer(customer);
                return true;
            }
        }
        return false;
    }

    public void forgotPassword(String email) throws EmailNotFoundException, EmailFailureException {
        Optional<Customer> opCustomer = customerDAO.findByEmailIgnoreCase(email);
        if (opCustomer.isPresent()) {
            Customer customer = opCustomer.get();
            String token = jwtService.generatePasswordResetJWT(customer);
            emailService.sendPasswordResetEmail(customer, token);

        } else {
            throw new EmailNotFoundException();
        }
    }

    public void resetPassword(PasswordResetBody body) {
        String email = jwtService.getResetPasswordEmail(body.getToken());
        Optional<Customer> opCustomer = customerDAO.findByEmailIgnoreCase(email);
        if (opCustomer.isPresent()) {
            Customer customer = opCustomer.get();
            customer.setPassword(encryptionService.encryptPassword(body.getPassword()));
            customerDAO.save(customer);
        }
    }

    public boolean userHasPermissionToUser(Customer customer, Long id) {
        return customer.getId() == id;
    }

    private VerificationToken createVerificationToken(Customer customer) {

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(jwtService.generateVerificationJWT(customer));
        verificationToken.setCreatedTimestamp(new Timestamp(System.currentTimeMillis()));
        verificationToken.setCustomer(customer);
        customer.getVerificationTokens().add(verificationToken);
        return verificationToken;
    }

    private void verifyEmail(Customer customer) {

        if (customer.isEmailVerified()) {
            getLogger().info("User [{}] email verified.", customer.getId());
            return;
        }

        getLogger().error("User [{}] email verification fail.", customer.getId());

        if (shouldResendVerification(customer)) {

            VerificationToken verificationToken = createVerificationToken(customer);
            verificationTokenDAO.save(verificationToken);
            emailService.sendVerificationEmail(verificationToken);
            getLogger().info("User [{}] email verification resent.", customer.getId());
        }

        throw new UserEmailNotVerifiedException(true);
    }

    private void verifyPassword(String rawPassword, Customer customer) {

        boolean isVerified = encryptionService.verifyPassword(rawPassword, customer.getPassword());

        if (isVerified) {
            getLogger().info("User [{}] password verified.", customer.getId());
        } else {
            throw new UserPasswordNotVerifiedException(
                    "User [%s] password verification fail.".formatted(customer.getUsername()));
        }
    }

    private boolean shouldResendVerification(Customer customer) {

        List<VerificationToken> tokens = customer.getVerificationTokens();
        return tokens.isEmpty() || tokens.get(0).getCreatedTimestamp()
                .before(new Timestamp(System.currentTimeMillis() - (60 * 60 * 1000)));
    }

}