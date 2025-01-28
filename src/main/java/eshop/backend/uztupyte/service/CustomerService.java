package eshop.backend.uztupyte.service;

import eshop.backend.uztupyte.api.model.LoginBody;
import eshop.backend.uztupyte.api.model.RegistrationBody;
import eshop.backend.uztupyte.api.model.VerificationToken;
import eshop.backend.uztupyte.exception.EmailFailureException;
import eshop.backend.uztupyte.exception.UserAlreadyExistsException;
import eshop.backend.uztupyte.exception.UserNotVerifiedException;
import eshop.backend.uztupyte.model.Customer;
import eshop.backend.uztupyte.model.dao.CustomerDAO;
import eshop.backend.uztupyte.model.dao.VerificationTokenDAO;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;


@Service
public class CustomerService {

    private CustomerDAO customerDAO;
    private VerificationTokenDAO verificationTokenDAO;
    private EncryptionService encryptionService;
    private JWTService jwtService;
    private EmailService emailService;


    public CustomerService(CustomerDAO customerDAO, EncryptionService encryptionService, JWTService jwtService,
                           EmailService emailService, VerificationTokenDAO verificationTokenDAO) {

        this.customerDAO = customerDAO;
        this.encryptionService = encryptionService;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.verificationTokenDAO = verificationTokenDAO;


    }


    public Customer registerCustomer(RegistrationBody registrationBody) throws UserAlreadyExistsException, EmailFailureException {

        if (customerDAO.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent()
                || customerDAO.findByUsernameIgnoreCase(registrationBody.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException();
        }

        Customer customer = new Customer();
        customer.setEmail(registrationBody.getEmail());
        customer.setFirstName(registrationBody.getFirstName());
        customer.setLastName(registrationBody.getLastName());
        customer.setUsername(registrationBody.getUsername());
        customer.setPassword(encryptionService.encryptPassword(registrationBody.getPassword()));
        VerificationToken verificationToken = createVerificationToken(customer);
        emailService.sendVerificationEmail(verificationToken);
        return customerDAO.save(customer);

    }

    private VerificationToken createVerificationToken(Customer customer) {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(jwtService.generateVerificationJWT(customer));
        verificationToken.setCreatedTimestamp(new Timestamp(System.currentTimeMillis()));
        verificationToken.setCustomer(customer);
        customer.getVerificationTokens().add(verificationToken);
        return verificationToken;
    }


    public String loginCustomer(LoginBody loginBody) throws UserNotVerifiedException, EmailFailureException {
        Optional<Customer> opCustomer = customerDAO.findByUsernameIgnoreCase(loginBody.getUsername());
        if (opCustomer.isPresent()) {
            Customer customer = opCustomer.get();
            if (encryptionService.verifyPassword(loginBody.getPassword(), customer.getPassword())) {
                if (customer.isEmailVerified()) {
                    return jwtService.generateJWT(customer);
                } else {
                    List<VerificationToken> verificationTokens = customer.getVerificationTokens();
                    boolean resend = verificationTokens.size() == 0 || verificationTokens.get(0).getCreatedTimestamp().before(new Timestamp(System.currentTimeMillis() - (60 * 60 * 1000)));
                    if (resend) {
                        VerificationToken verificationToken = createVerificationToken(customer);
                        verificationTokenDAO.save(verificationToken);
                        emailService.sendVerificationEmail(verificationToken);


                    }
                    throw new UserNotVerifiedException(resend);
                }

            }
        }
        return null;
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

}