package ru.hogwarts.school.exception;

public class IncorrectAgeException extends RuntimeException {
    public IncorrectAgeException(String message) {
        super(message);
    }
}
