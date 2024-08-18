package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class StudentService {

    private final Map<Long, Student> students = new HashMap<>();
    private long countId = 0;

    public Student createStudent(Student student) {
        student.setId(++countId);
        students.put(countId, student);
        return student;
    }

    public Student findStudent(long studentId) {
        return students.get(studentId);
    }

    public Student editStudent(Student student) {
        if (students.containsKey(student.getId())) {
            students.put(student.getId(), student);
            return student;
        }
        return null;
    }

    public Student deleteStudent(long studentId) {
        return students.remove(studentId);
    }

    public Collection<Student> getAllStudents() {
        return students.values();
    }
}
