package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.IncorrectAgeException;
import ru.hogwarts.school.exception.IncorrectIdException;
import ru.hogwarts.school.exception.ParameterIsNullException;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final Map<Long, Student> students = new HashMap<>();
    private long idCounter = 0;

    public Student createStudent(Student student) {
        notNullParameterChecker(student);
        student.setId(++idCounter);
        students.put(idCounter, student);
        return student;
    }

    public Student findStudent(long studentId) {
        idParameterChecker(studentId);
        return students.get(studentId);
    }

    public Student editStudent(Student student) {
        notNullParameterChecker(student);
        if (students.containsKey(student.getId())) {
            students.put(student.getId(), student);
            return student;
        }
        return null;
    }

    public Student deleteStudent(long studentId) {
        idParameterChecker(studentId);
        return students.remove(studentId);
    }

    public Collection<Student> getAllStudents() {
        return students.values();
    }

    public Collection<Student> getAllStudentsByAge(int studentAge) {
        ageParameterChecker(studentAge);
        return getAllStudents()
                .stream()
                .filter(e -> e.getAge() == studentAge)
                .collect(Collectors.toList());
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
