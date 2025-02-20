package eshop.backend.uztupyte.api.controller.auth;

import eshop.backend.uztupyte.api.model.LoginBody;
import eshop.backend.uztupyte.api.model.LoginResponse;
import eshop.backend.uztupyte.api.model.RegistrationBody;
import eshop.backend.uztupyte.service.CustomerService;
import eshop.backend.uztupyte.util.Loggable;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController implements Loggable {

    private final CustomerService customerService;

    public AuthenticationController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("register")
    public ResponseEntity<Void> registerCustomer(@Valid @RequestBody RegistrationBody registrationBody) {

        customerService.registerCustomer(registrationBody);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginBody loginBody) {

        String jwt = customerService.loginCustomer(loginBody);
        if (jwt == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        LoginResponse response = new LoginResponse();
        response.setJwt(jwt);
        response.setSuccess(true);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<Void> verifyEmail(@RequestParam String token) {

        if (customerService.verifyCustomer(token)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

//    TODO
//    @GetMapping("/me")
//    public Customer getLoggedInCustomer(@AuthenticationPrincipal Customer customer) {
//        return customer;
//    }
//
//    @PostMapping("/forgot")
//    public ResponseEntity<Void> forgotPassword(@RequestParam String email) {
//
//        customerService.forgotPassword(email);
//        return ResponseEntity.ok().build();
//    }
//
//    @PostMapping("/reset")
//    public ResponseEntity<Void> resetPassword(@Valid @RequestBody PasswordResetBody body) {
//
//        customerService.resetPassword(body);
//        return ResponseEntity.ok().build();
//    }
}
