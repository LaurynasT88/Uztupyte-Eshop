package eshop.backend.uztupyte.api.security;

import com.auth0.jwt.exceptions.JWTDecodeException;
import eshop.backend.uztupyte.model.Customer;
import eshop.backend.uztupyte.model.dao.CustomerDAO;
import eshop.backend.uztupyte.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

@Component
public class JWTRequestFilter extends OncePerRequestFilter {

    private JWTService jwtService;
    private CustomerDAO customerDAO;

    public JWTRequestFilter(JWTService jwtService, CustomerDAO customerDAO) {
        this.jwtService = jwtService;
        this.customerDAO = customerDAO;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String tokenHeader = request.getHeader("Authorization");
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            String token = tokenHeader.substring(7);

            try{
                String username = jwtService.getUsername(token);
                Optional<Customer> opUser = customerDAO.findByUsernameIgnoreCase(username);
             if (opUser.isPresent()) {
                 Customer customer = opUser.get();
                 if(customer.isEmailVerified()) {
                     UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(customer, null, new ArrayList<>());
                     authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                     SecurityContextHolder.getContext().setAuthentication(authentication);
                 }
             }
            }catch (JWTDecodeException ex){

            }
        }
        filterChain.doFilter(request, response);
    }
}
