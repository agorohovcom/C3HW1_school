package ru.hogwarts.school.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hogwarts.school.exception.IncorrectAgeException;
import ru.hogwarts.school.exception.IncorrectIdException;
import ru.hogwarts.school.exception.ParameterIsNullException;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @InjectMocks
    private StudentService out;

    @Mock
    private StudentRepository repositoryMock;

    @Test
    void shouldCreateStudentCorrectly() {
        Student expected = new Student(null, "Eddie", 55);
        out.createStudent(expected);
        verify(repositoryMock, times(1)).save(expected);
        assertThrows(ParameterIsNullException.class, () -> out.createStudent(null));
    }

    @Test
    void shouldFindStudentCorrectly() {
        Student expected = new Student(1L, "Aleksandr", 34);
        out.findStudent(1L);
        verify(repositoryMock, times(1)).findById(1L);
        assertThrows(IncorrectIdException.class, () -> out.findStudent(-1));
    }

    @Test
    void shouldEditStudentCorrectly() {
        Student expected = new Student(2L, "Nata", 25);
        out.editStudent(expected);
        verify(repositoryMock, times(1)).save(expected);
        assertThrows(ParameterIsNullException.class, () -> out.editStudent(null));
    }

    @Test
    void shouldDeleteStudentCorrectly() {
        Student expected = new Student(3L, "Bernadette", 21);
        out.deleteStudent(3L);
        verify(repositoryMock, times(1)).deleteById(3L);
        assertThrows(IncorrectIdException.class, () -> out.deleteStudent(-1));
    }

    @Test
    void shouldGetAllStudentsCorrectly() {
        out.getAllStudents();
        verify(repositoryMock, times(1)).findAll();
    }

    @Test
    void shouldGetAllStudentsByAgeCorrectly() {
        out.getAllStudentsByAge(35);
        verify(repositoryMock, times(1)).findAllByAge(35);
        assertThrows(IncorrectAgeException.class, () -> out.getAllStudentsByAge(-1));
    }
}