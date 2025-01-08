package eshopbackend.uztupyte1.api.controller.auth;

import eshopbackend.uztupyte1.api.model.RegistrationBody;
import eshopbackend.uztupyte1.service.CustomerService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {


    private CustomerService customerService;

    public AuthenticationController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("register")
    public void registerCustomer(@RequestBody RegistrationBody registrationBody) {
       customerService.registerCustomer(registrationBody);

    }

}
