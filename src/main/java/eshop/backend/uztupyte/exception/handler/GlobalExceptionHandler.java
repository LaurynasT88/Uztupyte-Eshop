package eshop.backend.uztupyte.exception.handler;

import eshop.backend.uztupyte.api.model.LoginResponse;
import eshop.backend.uztupyte.exception.EmailFailureException;
import eshop.backend.uztupyte.exception.EmailNotFoundException;
import eshop.backend.uztupyte.exception.UserAlreadyExistsException;
import eshop.backend.uztupyte.exception.UserEmailNotVerifiedException;
import eshop.backend.uztupyte.util.Loggable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler implements Loggable {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleUserAlreadyExists(UserAlreadyExistsException ex) {

        return buildErrorResponse(HttpStatus.BAD_REQUEST, "User already exists.");
    }

    @ExceptionHandler(EmailFailureException.class)
    public ResponseEntity<Map<String, String>> handleEmailFailure(EmailFailureException ex) {

        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send email.");
    }

    @ExceptionHandler(UserEmailNotVerifiedException.class)
    public ResponseEntity<LoginResponse> handleUserEmailNotVerified(UserEmailNotVerifiedException ex) {

        LoginResponse response = new LoginResponse();
        response.setSuccess(false);
        String reason = "User not verified. Please check your email for the verification link.";
        if (ex.isEmailResent()) {
            reason += " A new verification email has been resent.";
        }
        response.setFailureReason(reason);

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEmailNotFound(EmailNotFoundException ex) {

        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Email not found.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {

        //rethrow authentication related exception to let CustomAccessDeniedHandler
        //do the trick
        if (ex instanceof AccessDeniedException) {
            throw (AccessDeniedException) ex;
        }

        getLogger().error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
    }

    private ResponseEntity<Map<String, String>> buildErrorResponse(HttpStatus status, String message) {

        Map<String, String> response = new HashMap<>();
        response.put("error", message);
        response.put("status", String.valueOf(status.value()));
        response.put("timestamp", Instant.now().toString());
        return ResponseEntity.status(status).body(response);
    }
}

