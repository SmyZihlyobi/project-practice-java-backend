package xyz.demorgan.projectpractice.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtValidationException extends RuntimeException {
    public JwtValidationException(String message) {
        super(message);
        logError(message, null);
    }

    public JwtValidationException(String message, Throwable cause) {
        super(message, cause);
        logError(message, cause);
    }

    private void logError(String message, Throwable cause) {
        log.error("JWT Validation Error: {}", message, cause);
    }
}