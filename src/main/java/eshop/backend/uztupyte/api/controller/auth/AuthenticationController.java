package eshop.backend.uztupyte.api.controller.auth;

import eshop.backend.uztupyte.api.model.LoginBody;
import eshop.backend.uztupyte.api.model.LoginResponse;
import eshop.backend.uztupyte.api.model.PasswordResetBody;
import eshop.backend.uztupyte.api.model.RegistrationBody;
import eshop.backend.uztupyte.exception.EmailFailureException;
import eshop.backend.uztupyte.exception.EmailNotFoundException;
import eshop.backend.uztupyte.exception.UserAlreadyExistsException;
import eshop.backend.uztupyte.exception.UserEmailNotVerifiedException;
import eshop.backend.uztupyte.model.Customer;
import eshop.backend.uztupyte.service.CustomerService;
import eshop.backend.uztupyte.util.Loggable;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController implements Loggable {


    private CustomerService customerService;

    public AuthenticationController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("register")
    public ResponseEntity registerCustomer(@Valid @RequestBody RegistrationBody registrationBody) {

        try {
            customerService.registerCustomer(registrationBody);
            return ResponseEntity.ok().build();
        } catch (UserAlreadyExistsException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (EmailFailureException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginBody loginBody) {
        String jwt = null;
        try {
            jwt = customerService.loginCustomer(loginBody);
        } catch (UserEmailNotVerifiedException ex) {
            LoginResponse response = new LoginResponse();
            response.setSuccess(false);
            String reason = "USER_NOT_VERIFIED";
            if (ex.isNewEmailSent()) {
                reason += "_EMAIL_RESENT";
            }
            response.setFailureReason(reason);
            getLogger().info("Login failed. User [{}] not verified.", loginBody.getUsername());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (EmailFailureException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        if (jwt == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else {
            LoginResponse response = new LoginResponse();
            response.setJwt(jwt);
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity verifyEmail(@RequestParam String token) {
        if (customerService.verifyCustomer(token)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }


    }

    @GetMapping("/me")
    public Customer getLoggedInCustomer(@AuthenticationPrincipal Customer customer) {
        return customer;
    }

    @PostMapping("/forgot")
    public ResponseEntity forgotPassword(@RequestParam String email) {
        try {
            customerService.forgotPassword(email);
            return ResponseEntity.ok().build();
        } catch (EmailNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (EmailFailureException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/reset")
    public ResponseEntity resetPassword(@Valid @RequestBody PasswordResetBody body) {
        customerService.resetPassword(body);
        return ResponseEntity.ok().build();
    }

}
