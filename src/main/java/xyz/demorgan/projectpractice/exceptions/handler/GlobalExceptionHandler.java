package xyz.demorgan.projectpractice.exceptions.handler;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import xyz.demorgan.projectpractice.exceptions.NotFound;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@Component
public class GlobalExceptionHandler extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(@NotNull Throwable ex, @NotNull DataFetchingEnvironment env) {
        if (ex instanceof NotFound) {
            log.error("Not found exception: {} at {}", ex.getMessage(), LocalDateTime.now());
            return GraphqlErrorBuilder.newError(env)
                    .message(ex.getMessage() + " time: " + LocalDateTime.now())
                    .errorType(ErrorType.NOT_FOUND)
                    .build();
        }

        if (ex instanceof IllegalArgumentException) {
            log.error("Illegal argument exception: {} at {}", ex.getMessage(), LocalDateTime.now());
            return GraphqlErrorBuilder.newError(env)
                    .message(ex.getMessage())
                    .errorType(ErrorType.BAD_REQUEST)
                    .build();
        } else if (ex instanceof ConstraintViolationException violationEx) {
            return GraphqlErrorBuilder.newError()
                    .message("Validation error")
                    .errorType(ErrorType.BAD_REQUEST)
                    .extensions(violationEx.getConstraintViolations().stream()
                            .collect(Collectors.toMap(
                                    v -> v.getPropertyPath().toString(),
                                    ConstraintViolation::getMessage
                            )))
                    .build();
        } else if (ex instanceof WebExchangeBindException bindEx) {
            return handleBindException(bindEx, env);
        }

        return super.resolveToSingleError(ex, env);
    }


    private GraphQLError handleBindException(WebExchangeBindException ex, DataFetchingEnvironment env) {
        log.error("Binding error: {} at {}", ex.getMessage(), LocalDateTime.now());
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));

        return GraphqlErrorBuilder.newError(env)
                .message("Invalid request: " + message)
                .errorType(ErrorType.BAD_REQUEST)
                .build();
    }
}
