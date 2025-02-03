package eshop.backend.uztupyte.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;



@Configuration
public class WebSecurityConfig {

    private JWTRequestFilter jwtRequestFilter;

    public WebSecurityConfig(JWTRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .addFilterBefore(jwtRequestFilter, org.springframework.security.web.access.intercept.AuthorizationFilter.class) // Ensure the JWT filter is applied before AuthorizationFilter
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/product", "/auth/register", "/auth/login",
                                "/auth/verify", "/auth/forgot" , "/auth/reset", "/error").permitAll() // Allow unauthenticated access to these endpoints
                        .anyRequest().authenticated() // Require authentication for all other requests
                );
        return http.build();
    }



}
