package eshop.backend.uztupyte.config;

import eshop.backend.uztupyte.service.FakeMailSender;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
public class MailConfig {

    @Bean
    @ConditionalOnProperty(name = "uztupyte.backend.autoVerificationEnabled", havingValue = "true")
    public JavaMailSender fakeMailSender() {

        return new FakeMailSender();
    }
}