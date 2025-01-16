package eshopbackend.uztupyte1.api.controller.auth;

import eshopbackend.uztupyte1.api.model.RegistrationBody;
import eshopbackend.uztupyte1.exception.UserAlreadyExistsException;
import eshopbackend.uztupyte1.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {


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
        }

    }

}
