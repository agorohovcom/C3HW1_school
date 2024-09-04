package ru.hogwarts.school.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class HogwartsExceptionHandler {

    @ExceptionHandler({
            IncorrectAgeException.class,
            IncorrectIdException.class,
            ParameterIsNullException.class
    })
    public ResponseEntity<String> handleBadRequest(RuntimeException re) {
        re.printStackTrace();
        return ResponseEntity.badRequest().body(re.getMessage());
    }

    @ExceptionHandler({
            StudentNotFoundException.class,
            FacultyNotFoundException.class,
            AvatarNotFoundException.class
    })
    public ResponseEntity<String> handleNotFound(RuntimeException re) {
        re.printStackTrace();
        return ResponseEntity.notFound().build();
    }
}
