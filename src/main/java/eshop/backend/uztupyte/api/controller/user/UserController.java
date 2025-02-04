package eshop.backend.uztupyte.api.controller.user;

import eshop.backend.uztupyte.model.Address;
import eshop.backend.uztupyte.model.Customer;
import eshop.backend.uztupyte.model.dao.AddressDAO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    private AddressDAO addressDAO;

    public UserController(AddressDAO addressDAO) {
        this.addressDAO = addressDAO;

    }

    @GetMapping("/{customerId}/address")
    public ResponseEntity<List<Address>> getAddress(
            @AuthenticationPrincipal Customer customer, @PathVariable Long customerId) {
        if (!userHasPermission(customer, customerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(addressDAO.findByCustomer_Id(customerId));
    }

    @PutMapping("{customerId}/address")
    public ResponseEntity<Address> putAddress(
            @AuthenticationPrincipal Customer customer,
            @PathVariable Long customerId, @RequestBody Address address) {
        if (!userHasPermission(customer, customerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        address.setId(null);
        Customer refUser = new Customer();
        refUser.setId(customerId);
        address.setCustomer(refUser);
        return ResponseEntity.ok(addressDAO.save(address));
    }

    @PatchMapping("{customerId}/address/{addressId}")
    public ResponseEntity<Address> patchAddress(
            @AuthenticationPrincipal Customer customer,
            @PathVariable Long customerId, @PathVariable Long addressId,
            @RequestBody Address address){
        if (!userHasPermission(customer, customerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (address.getId() == addressId) {
            Optional<Address> opOriginalAddress = addressDAO.findById(addressId);
            if (opOriginalAddress.isPresent()) {
                if (opOriginalAddress.get().getCustomer().getId() == customerId) {
                    Customer originalCustomer = opOriginalAddress.get().getCustomer();
                    if(originalCustomer.getId() == customerId)
                    address.setCustomer(originalCustomer);
                    return ResponseEntity.ok( addressDAO.save(address));
                }
            }
        }
        return ResponseEntity.badRequest().build();
    }




    private boolean userHasPermission(Customer customer, Long id) {
        return customer.getId() == id;
    }


}
