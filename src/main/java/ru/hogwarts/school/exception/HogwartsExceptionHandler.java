package ru.hogwarts.school.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class HogwartsExceptionHandler {

    Logger log = LoggerFactory.getLogger(HogwartsExceptionHandler.class);

    @ExceptionHandler({
            IncorrectAgeException.class,
            IncorrectIdException.class,
            ParameterIsNullException.class
    })
    public ResponseEntity<String> handleBadRequest(RuntimeException re) {
        log.error("handleBadRequest thrown with message: {}", re.getMessage());
        return ResponseEntity.badRequest().body(re.getMessage());
    }

    @ExceptionHandler({
            StudentNotFoundException.class,
            FacultyNotFoundException.class,
            AvatarNotFoundException.class
    })
    public ResponseEntity<String> handleNotFound(RuntimeException re) {
        log.error("handleNotFound thrown with message: {}", re.getMessage());
        return ResponseEntity.notFound().build();
    }
}
