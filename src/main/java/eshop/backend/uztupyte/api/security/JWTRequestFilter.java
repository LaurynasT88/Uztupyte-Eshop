package eshop.backend.uztupyte.api.security;

import com.auth0.jwt.exceptions.JWTDecodeException;
import eshop.backend.uztupyte.model.Customer;
import eshop.backend.uztupyte.model.dao.CustomerDAO;
import eshop.backend.uztupyte.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class JWTRequestFilter extends OncePerRequestFilter implements ChannelInterceptor {

    private JWTService jwtService;
    private CustomerDAO customerDAO;

    public JWTRequestFilter(JWTService jwtService, CustomerDAO customerDAO) {
        this.jwtService = jwtService;
        this.customerDAO = customerDAO;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String tokenHeader = request.getHeader("Authorization");
        UsernamePasswordAuthenticationToken token = checkToken(tokenHeader);
        if (token != null) {
            token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        }
        filterChain.doFilter(request, response);
    }
    private UsernamePasswordAuthenticationToken checkToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);

            try{
                String username = jwtService.getUsername(token);
                Optional<Customer> opUser = customerDAO.findByUsernameIgnoreCase(username);
                if (opUser.isPresent()) {
                    Customer customer = opUser.get();
                    if(customer.isEmailVerified()) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(customer, null, new ArrayList<>());

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        return authentication;
                    }
                }
            }catch (JWTDecodeException ex){

            }
        }
        SecurityContextHolder.getContext().setAuthentication(null);
        return null;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        if (message.getHeaders().get("simpMessageType").equals(SimpMessageType.CONNECT)) {
            Map nativeHeaders = (Map) message.getHeaders().get("nativeHeaders");
            if (nativeHeaders != null) {
                List authTokenList = (List) nativeHeaders.get("Authorization");
                if (authTokenList != null) {
                    String tokenHeader = (String) authTokenList.get(0);
                    checkToken(tokenHeader);
                }

            }
        }
        return message;

    }
}
