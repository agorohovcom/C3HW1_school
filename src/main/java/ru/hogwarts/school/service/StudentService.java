package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.IncorrectAgeException;
import ru.hogwarts.school.exception.IncorrectIdException;
import ru.hogwarts.school.exception.ParameterIsNullException;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;

@Service
public class StudentService {

    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    public Student createStudent(Student student) {
        notNullParameterChecker(student);
        return repository.save(student);
    }

    public Student findStudent(long studentId) {
        idParameterChecker(studentId);
        return repository.findById(studentId).orElseGet(() -> null);
    }

    public Student editStudent(Student student) {
        notNullParameterChecker(student);
        return repository.save(student);
    }

    public void deleteStudent(long studentId) {
        idParameterChecker(studentId);
        repository.deleteById(studentId);
    }

    public Collection<Student> getAllStudents() {
        return repository.findAll();
    }

    public Collection<Student> getAllStudentsByAge(int studentAge) {
        ageParameterChecker(studentAge);
        return repository.findAllByAge(studentAge);
    }

    public Collection<Student> findByAgeBetween(int min, int max) {
        ageParameterChecker(min);
        ageParameterChecker(max);
        return repository.findByAgeBetween(min, max);
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
