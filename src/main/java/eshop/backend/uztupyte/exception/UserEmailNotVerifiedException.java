package eshop.backend.uztupyte.exception;

public class UserEmailNotVerifiedException extends RuntimeException {

    private final boolean newEmailSent;

    public UserEmailNotVerifiedException(boolean newEmailSent) {
        this.newEmailSent = newEmailSent;
    }


    public boolean isNewEmailSent() {
        return newEmailSent;
    }
}
