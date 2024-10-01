package ru.hogwarts.school.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hogwarts.school.dto.FacultyDto;
import ru.hogwarts.school.dto.StudentDto;
import ru.hogwarts.school.exception.IncorrectAgeException;
import ru.hogwarts.school.exception.IncorrectIdException;
import ru.hogwarts.school.exception.ParameterIsNullException;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.hogwarts.school.constants.Constants.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @InjectMocks
    private StudentService out;

    @Mock
    private StudentRepository studentRepositoryMock;
    @Mock
    private FacultyService facultyServiceMock;

    @Test
    void createTest() {
        when(facultyServiceMock.findByName(anyString())).thenReturn(FACULTY_DTO);
        when(studentRepositoryMock.save(any(Student.class))).thenReturn(STUDENT_1_SAM);

        StudentDto result = out.create(STUDENT_DTO_1_SAM, FACULTY.getName());

        assertNotNull(result);
        assertEquals(STUDENT_DTO_1_SAM.getName(), result.getName());
        assertEquals(STUDENT_DTO_1_SAM.getAge(), result.getAge());
        verify(studentRepositoryMock, times(1)).save(any(Student.class));

        assertThrows(ParameterIsNullException.class, () -> out.create(null, FACULTY.getName()));
        assertThrows(ParameterIsNullException.class, () -> out.create(STUDENT_DTO_1_SAM, null));
    }

    @Test
    void createWithRandomFacultyTest() {
        when(facultyServiceMock.findRandom()).thenReturn(FACULTY_DTO);
        when(studentRepositoryMock.save(any(Student.class))).thenReturn(STUDENT_1_SAM);

        StudentDto result = out.createWithRandomFaculty(STUDENT_DTO_1_SAM);

        assertNotNull(result);
        assertEquals(STUDENT_DTO_1_SAM.getName(), result.getName());
        assertEquals(STUDENT_DTO_1_SAM.getAge(), result.getAge());
        verify(studentRepositoryMock, times(1)).save(any(Student.class));

        assertThrows(ParameterIsNullException.class, () -> out.createWithRandomFaculty(null));
    }

    @Test
    void findByIdTest() {
        when(studentRepositoryMock.findById(anyLong())).thenReturn(Optional.ofNullable(STUDENT_1_SAM));

        StudentDto result = out.findById(1L);

        assertNotNull(result);
        assertEquals(STUDENT_DTO_1_SAM.getName(), result.getName());
        assertEquals(STUDENT_DTO_1_SAM.getAge(), result.getAge());
        verify(studentRepositoryMock, times(1)).findById(1L);

        when(studentRepositoryMock.findById(1L)).thenReturn(Optional.empty());
        assertThrows(StudentNotFoundException.class, () -> out.findById(1L));

        assertThrows(IncorrectIdException.class, () -> out.findById(-1L));
    }

    @Test
    void editTest() {
        when(studentRepositoryMock.save(any(Student.class))).thenReturn(STUDENT_1_SAM);
        when(studentRepositoryMock.findById(anyLong())).thenReturn(Optional.ofNullable(STUDENT_1_SAM));

        StudentDto result = out.edit(STUDENT_DTO_1_SAM);

        assertNotNull(result);
        assertEquals(STUDENT_DTO_1_SAM.getName(), result.getName());
        assertEquals(STUDENT_DTO_1_SAM.getAge(), result.getAge());
        verify(studentRepositoryMock, times(1)).save(any(Student.class));

        assertThrows(ParameterIsNullException.class, () -> out.edit(null));
    }

    @Test
    void deleteTest() {
        when(studentRepositoryMock.findById(anyLong())).thenReturn(Optional.ofNullable(STUDENT_1_SAM));

        out.delete(1L);

        verify(studentRepositoryMock, times(1)).findById(anyLong());

        assertThrows(IncorrectIdException.class, () -> out.delete(-1L));
        assertThrows(IncorrectIdException.class, () -> out.delete(0L));
    }

    @Test
    void getAllTest() {
        when(studentRepositoryMock.findAll()).thenReturn(List.of(STUDENT_1_SAM));

        Collection<StudentDto> result = out.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(studentRepositoryMock, times(1)).findAll();
    }

    @Test
    void getAllByAgeTest() {
        when(studentRepositoryMock.findAllByAge(anyInt())).thenReturn(List.of(STUDENT_1_SAM));

        Collection<StudentDto> result = out.getAllByAge(STUDENT_1_SAM.getAge());

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(studentRepositoryMock, times(1)).findAllByAge(anyInt());

        assertThrows(IncorrectAgeException.class, () -> out.getAllByAge(0));
        assertThrows(IncorrectAgeException.class, () -> out.getAllByAge(-1));
    }

    @Test
    void findByAgeBetweenTest() {
        when(studentRepositoryMock.findByAgeBetween(anyInt(), anyInt())).thenReturn(List.of(STUDENT_1_SAM, STUDENT_2_AARON));

        Collection<StudentDto> result = out.findByAgeBetween(5, 50);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(studentRepositoryMock, times(1)).findByAgeBetween(anyInt(), anyInt());

        assertThrows(IncorrectAgeException.class, () -> out.findByAgeBetween(0, 24));
        assertThrows(IncorrectAgeException.class, () -> out.findByAgeBetween(-1, 24));
        assertThrows(IncorrectAgeException.class, () -> out.findByAgeBetween(24, 0));
        assertThrows(IncorrectAgeException.class, () -> out.findByAgeBetween(24, -1));
    }

    @Test
    void findFacultyByStudentIdTest() {
        when(studentRepositoryMock.findById(anyLong())).thenReturn(Optional.ofNullable(STUDENT_1_SAM));

        FacultyDto result = out.findFacultyByStudentId(1L);

        assertNotNull(result);
        assertEquals(FACULTY_DTO.getName(), result.getName());
        assertEquals(FACULTY_DTO.getColor(), result.getColor());
        assertEquals(FACULTY_DTO.getStudents(), result.getStudents());

        assertThrows(IncorrectIdException.class, () -> out.findFacultyByStudentId(-1));
        assertThrows(IncorrectIdException.class, () -> out.findFacultyByStudentId(0));
    }

    @Test
    void findNamesStartsWithAAscUpperCaseTest() {
        when(studentRepositoryMock.findAll()).thenReturn(List.of(
                STUDENT_1_SAM,
                STUDENT_2_AARON,
                STUDENT_3_AARON,
                STUDENT_4_DIK,
                STUDENT_5_ALBERT,
                STUDENT_6_GENNADIY
        ));

        Collection<String> expected = List.of(
                STUDENT_DTO_2_AARON.getName().toUpperCase(),
                STUDENT_DTO_5_ALBERT.getName().toUpperCase()
        );
        Collection<String> actual = out.findNamesStartsWithAAscUpperCase();

        assertIterableEquals(expected, actual);
    }

    @Test
    void findNamesStartsWithAAscUpperCaseWhenThereAreNoStudentsTest() {
        when(studentRepositoryMock.findAll()).thenReturn(List.of());

        int expectedSize = 0;
        int actualSize = out.findNamesStartsWithAAscUpperCase().size();

        assertEquals(expectedSize, actualSize);
    }

    @Test
    void getAvgAgeTest() {
        List<Student> students = List.of(
                STUDENT_1_SAM,
                STUDENT_4_DIK,
                STUDENT_5_ALBERT
        );

        when(studentRepositoryMock.findAll()).thenReturn(students);

        Double avgAge = students
                .stream()
                .mapToDouble(Student::getAge)
                .average()
                .getAsDouble();

        String expected = String.format("%.2f", avgAge);
        String actual = out.getAvgAge();

        assertEquals(expected, actual);
    }

    @Test
    void getAvgAgeWhenThereAreNoStudentsTest() {
        when(studentRepositoryMock.findAll()).thenReturn(List.of());

        assertThrows(StudentNotFoundException.class, () -> out.getAvgAge());
    }

    @Test
    void printParallelTest() {
        List<Student> students = List.of(
                STUDENT_1_SAM,
                STUDENT_2_AARON,
                STUDENT_3_AARON,
                STUDENT_4_DIK,
                STUDENT_5_ALBERT,
                STUDENT_6_GENNADIY
        );

        when(studentRepositoryMock.findAll()).thenReturn(students);

        out.printParallel();
        verify(studentRepositoryMock, times(1)).findAll();
    }

    @Test
    void printParallelNotEnoughStudentsTest() {
        when(studentRepositoryMock.findAll()).thenReturn(List.of(STUDENT_1_SAM));
        assertThrows(RuntimeException.class, () -> out.printParallel());
    }

    @Test
    void printSynchronizedTest() {
        List<Student> students = List.of(
                STUDENT_1_SAM,
                STUDENT_2_AARON,
                STUDENT_3_AARON,
                STUDENT_4_DIK,
                STUDENT_5_ALBERT,
                STUDENT_6_GENNADIY
        );

        when(studentRepositoryMock.findAll()).thenReturn(students);

        out.printSynchronized();
        verify(studentRepositoryMock, times(1)).findAll();
    }

    @Test
    void printSynchronizedNotEnoughStudentsTest() {
        when(studentRepositoryMock.findAll()).thenReturn(List.of(STUDENT_1_SAM));
        assertThrows(RuntimeException.class, () -> out.printSynchronized());
    }
}