package xyz.demorgan.projectpractice.exceptions.handler;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import xyz.demorgan.projectpractice.exceptions.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String TIMESTAMP = "timestamp";
    private static final String STATUS = "status";
    private static final String ERROR = "error";
    private static final String MESSAGE = "message";
    private static final String ERRORS = "errors";

    private Map<String, Object> createErrorBody(HttpStatus status, String error, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIMESTAMP, LocalDateTime.now());
        body.put(STATUS, status.value());
        body.put(ERROR, error);
        body.put(MESSAGE, message);
        return body;
    }
    private Map<String, Object> createErrorBodyWithErrors(HttpStatus status, String error,
                                                          String message, List<String> errors) {
        Map<String, Object> body = createErrorBody(status, error, message);
        body.put(ERRORS, errors);
        return body;
    }

    @ExceptionHandler(NotFound.class)
    public ResponseEntity<Object> handleNotFoundException(NotFound ex) {
        return new ResponseEntity<>(
                createErrorBody(HttpStatus.NOT_FOUND, "Resource Not Found", ex.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(JwtValidationException.class)
    public ResponseEntity<Object> handleJwtValidationException(JwtValidationException ex) {
        return new ResponseEntity<>(
                createErrorBody(HttpStatus.UNAUTHORIZED, "Authentication Error", ex.getMessage()),
                HttpStatus.UNAUTHORIZED
        );
    }

    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        return new ResponseEntity<>(
                createErrorBodyWithErrors(HttpStatus.BAD_REQUEST, "Validation Error", "Invalid request content", errors),
                HttpStatus.BAD_REQUEST
        );
    }

    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                         HttpHeaders headers,
                                                                         HttpStatus status,
                                                                         WebRequest request) {
        return new ResponseEntity<>(
                createErrorBody(HttpStatus.METHOD_NOT_ALLOWED, "Method Not Allowed", ex.getMessage()),
                HttpStatus.METHOD_NOT_ALLOWED
        );
    }

    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
                                                                     HttpHeaders headers,
                                                                     HttpStatus status,
                                                                     WebRequest request) {
        return new ResponseEntity<>(
                createErrorBody(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported Media Type", ex.getMessage()),
                HttpStatus.UNSUPPORTED_MEDIA_TYPE
        );
    }

    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex,
                                                                   HttpHeaders headers,
                                                                   HttpStatus status,
                                                                   WebRequest request) {
        return new ResponseEntity<>(
                createErrorBody(HttpStatus.NOT_FOUND, "Endpoint Not Found",
                        "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex) {
        return new ResponseEntity<>(
                createErrorBody(HttpStatus.FORBIDDEN, "Access Denied", "Insufficient permissions"),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(
                createErrorBody(HttpStatus.BAD_REQUEST, "Invalid Argument", ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex) {
        return new ResponseEntity<>(
                createErrorBody(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                        "An unexpected error occurred"),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}