package eshop.backend.uztupyte.exception;

public class UserEmailNotVerifiedException extends RuntimeException {

    private final boolean isEmailResent;

    public UserEmailNotVerifiedException(String message, boolean isEmailResent) {

        super(message);
        this.isEmailResent = isEmailResent;
    }

    public boolean isEmailResent() {

        return isEmailResent;
    }
}
