package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.dto.StudentDto;
import ru.hogwarts.school.exception.IncorrectAgeException;
import ru.hogwarts.school.exception.IncorrectIdException;
import ru.hogwarts.school.exception.ParameterIsNullException;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    public StudentDto createStudent(StudentDto studentDto) {
        notNullParameterChecker(studentDto);
        return StudentDto.toDto(repository.save(StudentDto.toNewEntity(studentDto)));
    }

    public StudentDto findStudent(long studentId) {
        idParameterChecker(studentId);
        return repository
                .findById(studentId)
                .map(StudentDto::toDto)
                .orElse(null);
    }

    public StudentDto editStudent(StudentDto studentDto) {
        notNullParameterChecker(studentDto);
        return StudentDto.toDto(repository.save(StudentDto.toEntity(studentDto)));
    }

    public void deleteStudent(long studentId) {
        idParameterChecker(studentId);
        repository.deleteById(studentId);
    }

    public Collection<StudentDto> getAllStudents() {
        return repository
                .findAll()
                .stream()
                .map(StudentDto::toDto)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Collection<StudentDto> getAllStudentsByAge(int studentAge) {
        ageParameterChecker(studentAge);
        return repository
                .findAllByAge(studentAge)
                .stream()
                .map(StudentDto::toDto)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Collection<StudentDto> findByAgeBetween(int min, int max) {
        ageParameterChecker(min);
        ageParameterChecker(max);
        return repository
                .findByAgeBetween(min, max)
                .stream()
                .map(StudentDto::toDto)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private void notNullParameterChecker(Object o) {
        if (o == null) {
            throw new ParameterIsNullException("Параметр не может быть null");
        }
    }

    private void idParameterChecker(long id) {
        if (id < 1) {
            throw new IncorrectIdException("ID не может быть меньше 1");
        }
    }

    private void ageParameterChecker(int age) {
        if (age < 1) {
            throw new IncorrectAgeException("Возраст не может быть меньше 1");
        }
    }
}
