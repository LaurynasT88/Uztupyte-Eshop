package eshop.backend.uztupyte.service;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "uztupyte.backend")
public class RegistrationProperties {

    private boolean autoVerificationEnabled;

    public boolean isAutoVerificationEnabled() {
        return autoVerificationEnabled;
    }

    public void setAutoVerificationEnabled(boolean autoVerificationEnabled) {
        this.autoVerificationEnabled = autoVerificationEnabled;
    }
}
