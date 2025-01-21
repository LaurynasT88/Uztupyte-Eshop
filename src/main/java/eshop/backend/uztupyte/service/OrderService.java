package eshop.backend.uztupyte.service;

import eshop.backend.uztupyte.model.Customer;
import eshop.backend.uztupyte.model.WebOrder;
import eshop.backend.uztupyte.model.dao.WebOrderDAO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private WebOrderDAO webOrderDAO;

    public OrderService(WebOrderDAO webOrderDAO) {
        this.webOrderDAO = webOrderDAO;
    }

    public List<WebOrder> getOrders(Customer customer) {
        return webOrderDAO.findByCustomer(customer);
    }
}
