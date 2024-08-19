package ru.hogwarts.school.exception;

public class IncorrectIdException extends RuntimeException {
    public IncorrectIdException(String message) {
        super(message);
    }
}
