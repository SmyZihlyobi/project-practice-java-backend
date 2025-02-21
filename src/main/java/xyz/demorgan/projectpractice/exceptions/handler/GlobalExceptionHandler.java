package xyz.demorgan.projectpractice.exceptions.handler;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;
import xyz.demorgan.projectpractice.exceptions.NotFound;

import java.time.LocalDateTime;

@Slf4j
@Component
public class GlobalExceptionHandler extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(@NotNull Throwable ex, @NotNull DataFetchingEnvironment env) {
        if (ex instanceof NotFound) {
            log.error("Not found exception: {} at {}", ex.getMessage(), LocalDateTime.now());
            return GraphqlErrorBuilder.newError(env)
                    .message(ex.getMessage() + " time: " +  LocalDateTime.now())
                    .errorType(ErrorType.NOT_FOUND)
                    .build();
        }

        if (ex instanceof IllegalArgumentException) {
            log.error("Illegal argument exception: {} at {}", ex.getMessage(), LocalDateTime.now());
            return GraphqlErrorBuilder.newError(env)
                    .message(ex.getMessage() + " time: " +  LocalDateTime.now())
                    .errorType(ErrorType.BAD_REQUEST)
                    .build();
        }
        return super.resolveToSingleError(ex, env);
    }
}
