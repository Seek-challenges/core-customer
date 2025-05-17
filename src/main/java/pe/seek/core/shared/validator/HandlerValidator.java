package pe.seek.core.shared.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HandlerValidator {

    private final Validator validator;

    public <T> Mono<T> validate(T target) {
        return validate(target, new Class<?>[0]);
    }

    public <T> Mono<T> validate(T target, Class<?>... groups) {
        Set<ConstraintViolation<T>> violations = validator.validate(target, groups);

        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));

            return Mono.error(new ConstraintViolationException(message, violations));
        }

        return Mono.just(target);
    }
}
