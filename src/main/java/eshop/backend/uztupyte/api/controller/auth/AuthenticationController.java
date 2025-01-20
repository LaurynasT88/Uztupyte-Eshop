package eshop.backend.uztupyte.api.controller.auth;

import eshop.backend.uztupyte.api.model.LoginBody;
import eshop.backend.uztupyte.api.model.LoginResponse;
import eshop.backend.uztupyte.api.model.RegistrationBody;
import eshop.backend.uztupyte.exception.UserAlreadyExistsException;
import eshop.backend.uztupyte.service.CustomerService;
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

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginCustomer(@Valid @RequestBody LoginBody loginBody) {
        String jwt = customerService.loginCustomer(loginBody);
        if (jwt == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }else{
            LoginResponse response = new LoginResponse();
            response.setJwt(jwt);
            return ResponseEntity.ok(response);

        }
    }

}
