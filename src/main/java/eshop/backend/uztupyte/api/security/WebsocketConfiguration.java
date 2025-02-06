package eshop.backend.uztupyte.api.security;

import eshop.backend.uztupyte.model.Customer;
import eshop.backend.uztupyte.service.CustomerService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Map;

@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
public class WebsocketConfiguration implements WebSocketMessageBrokerConfigurer {

    private final CustomerService customerService;
    private ApplicationContext context;
    private  JWTRequestFilter jwtRequestFilter;
    private static  final AntPathMatcher MATCHER = new AntPathMatcher();

    public WebsocketConfiguration(ApplicationContext context,
                                  JWTRequestFilter jwtRequestFilter, CustomerService customerService) {
        this.context = context;
        this.jwtRequestFilter = jwtRequestFilter;
        this.customerService = customerService;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket").setAllowedOrigins("**").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(
            jwtRequestFilter, 
            new ChannelInterceptor() {
                @Override
                public Message<?> preSend(Message<?> message, MessageChannel channel) {
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    if (authentication != null && authentication.isAuthenticated()) {
                        return message;
                    }
                    throw new AccessDeniedException("User not authenticated");
                }
            },
            new RejectClientMessagesOnChannelInterceptor(),
            new DestinationLevelAuthorizationChannelInterceptor()
        );
    }
    private class RejectClientMessagesOnChannelInterceptor
            implements ChannelInterceptor {

        private String []  paths = new String [] {
          "/topic/user/*/address"
        };

        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {
            if(message.getHeaders().get("simpMessageType").equals(SimpMessageType.MESSAGE)) {
                String destination = (String) message.getHeaders().get("simpDestination");
                for (String path : paths) {
                    if (MATCHER.match(path, destination))
                        message = null;
                }
            }
            return message;
        }

    }

    private class  DestinationLevelAuthorizationChannelInterceptor implements ChannelInterceptor {

        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {
            if (message.getHeaders().get("simpMessageType").equals(SimpMessageType.SUBSCRIBE)) {
                String destination = (String) message.getHeaders().get("simpDestination");
                Map<String, String> params = MATCHER.extractUriTemplateVariables(
                        "/topic/user/{userId}/**" , destination);
                try{
                    Long userId = Long.valueOf(params.get("userId"));
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    if (authentication != null) {
                        Customer customer = (Customer) authentication.getPrincipal();
                        if (!customerService.userHasPermissionToUser(customer, userId)) {
                            message = null;
                        }
                    }else {
                        message = null;
                    }


                } catch (NumberFormatException ex){
                    message = null;
             }
            }
            return message;
        }

    }
}
