package eshop.backend.uztupyte.api.controller.order;


import eshop.backend.uztupyte.model.Customer;
import eshop.backend.uztupyte.model.WebOrder;
import eshop.backend.uztupyte.service.OrderService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    private OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<WebOrder> getOrders(@AuthenticationPrincipal Customer customer) {
        return orderService.getOrders(customer);
    }
    //TODO make order place endpoint protected with user role
}
