package ru.hogwarts.school.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.hogwarts.school.exception.IncorrectAgeException;
import ru.hogwarts.school.exception.IncorrectIdException;
import ru.hogwarts.school.exception.ParameterIsNullException;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StudentServiceTest {

    StudentService out;

    @BeforeEach
    void setUp() {
        out = new StudentService();
        out.createStudent(new Student(0L, "Aleksandr", 34));
        out.createStudent(new Student(0L, "Paul", 17));
        out.createStudent(new Student(0L, "Bernadette", 21));
        out.createStudent(new Student(0L, "Lucinda", 46));
        out.createStudent(new Student(0L, "Zinaida", 34));
    }

    @Test
    void shouldCreateStudentCorrectly() {
        Student expected1 = new Student(null, "Eddie", 55);
        Student expected2 = new Student(66L, "Bilbo", 99);
        assertEquals(expected1, out.createStudent(expected1));
        assertEquals(expected2, out.createStudent(expected2));
        assertThrows(ParameterIsNullException.class, () -> out.createStudent(null));
    }

    @Test
    void shouldFindStudentCorrectly() {
        Student expected = new Student(1L, "Aleksandr", 34);
        assertEquals(expected, out.findStudent(1));
        assertNull(out.findStudent(333));
        assertThrows(IncorrectIdException.class, () -> out.findStudent(-1));
    }

    @Test
    void shouldEditStudentCorrectly() {
        Student exception = new Student(2L, "Nata", 25);
        assertEquals(exception, out.editStudent(exception));
        assertNull(out.editStudent(new Student(300L, "Roland", 666)));
        assertThrows(ParameterIsNullException.class, () -> out.editStudent(null));
    }

    @Test
    void shouldDeleteStudentCorrectly() {
        Student expected = new Student(3L, "Bernadette", 21);
        int expectedSize = out.getAllStudents().size() - 1;
        assertEquals(expected, out.deleteStudent(3L));
        assertEquals(expectedSize, out.getAllStudents().size());
        assertThrows(IncorrectIdException.class, () -> out.deleteStudent(-1));
        assertNull(out.deleteStudent(555L));
    }

    @Test
    void shouldGetAllStudentsCorrectly() {
        Collection<Student> expected = List.of(
                new Student(1L, "Aleksandr", 34),
                new Student(2L, "Paul", 17),
                new Student(3L, "Bernadette", 21),
                new Student(4L, "Lucinda", 46),
                new Student(5L, "Zinaida", 34)
        );
        assertIterableEquals(expected, out.getAllStudents());
    }

    @Test
    void shouldGetAllStudentsByAgeCorrectly() {
        Collection<Student> expected = List.of(
                new Student(1L, "Aleksandr", 34),
                new Student(5L, "Zinaida", 34)
        );
        assertIterableEquals(expected, out.getAllStudentsByAge(34));
        assertIterableEquals(List.of(), out.getAllStudentsByAge(1000));
        assertThrows(IncorrectAgeException.class, () -> out.getAllStudentsByAge(-1));
    }
}