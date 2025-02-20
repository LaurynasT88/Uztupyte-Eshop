package eshop.backend.uztupyte.api.controller.user;

import eshop.backend.uztupyte.model.Customer;
import eshop.backend.uztupyte.model.dao.AddressDAO;
import eshop.backend.uztupyte.model.dao.CustomerDAO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final AddressDAO addressDAO;
    private final CustomerDAO customerDAO;


    public UserController(AddressDAO addressDAO, CustomerDAO customerDAO) {
        this.addressDAO = addressDAO;
        this.customerDAO = customerDAO;
    }
//TODO implement addresses fetching in front end

//    @GetMapping("/{customerId}/address")
//    public ResponseEntity<List<Address>> getAddress(
//            @AuthenticationPrincipal Customer customer, @PathVariable Long customerId) {
//        if (!userHasPermission(customer, customerId)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }
//        return ResponseEntity.ok(addressDAO.findByCustomer_Id(customerId));
//    }

//    @PutMapping("{customerId}/address")
//    public ResponseEntity<Address> putAddress(
//            @AuthenticationPrincipal Customer customer,
//            @PathVariable Long customerId, @RequestBody Address address) {
//        if (!userHasPermission(customer, customerId)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }
//        address.setId(null);
//        Customer refUser = new Customer();
//        refUser.setId(customerId);
//        address.setCustomer(refUser);
//        return ResponseEntity.ok(addressDAO.save(address));
//    }
//
//    @PatchMapping("{customerId}/address/{addressId}")
//    public ResponseEntity<Address> patchAddress(
//            @AuthenticationPrincipal Customer customer,
//            @PathVariable Long customerId, @PathVariable Long addressId,
//            @RequestBody Address address){
//        if (!userHasPermission(customer, customerId)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }
//        if (address.getId() == addressId) {
//            Optional<Address> opOriginalAddress = addressDAO.findById(addressId);
//            if (opOriginalAddress.isPresent()) {
//                if (opOriginalAddress.get().getCustomer().getId() == customerId) {
//                    Customer originalCustomer = opOriginalAddress.get().getCustomer();
//                    if(originalCustomer.getId() == customerId)
//                    address.setCustomer(originalCustomer);
//                    return ResponseEntity.ok( addressDAO.save(address));
//                }
//            }
//        }
//        return ResponseEntity.badRequest().build();
//    }

    @PreAuthorize("hasRole('ADMIN')") // Ensure only admins can access this
    @GetMapping()
    public ResponseEntity<List<Customer>> getAllUsers() {
        List<Customer> customers = customerDAO.findAll();

        return ResponseEntity.ok(customers);
    }

}
