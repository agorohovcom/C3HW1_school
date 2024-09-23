package ru.hogwarts.school.service;

import org.junit.jupiter.api.BeforeEach;
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
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @InjectMocks
    private StudentService out;

    @Mock
    private StudentRepository studentRepositoryMock;
    @Mock
    private FacultyService facultyServiceMock;

    private StudentDto studentDto;
    private FacultyDto facultyDto;
    private Student student;
    private Faculty faculty;

    @BeforeEach
    void setUp() {
        facultyDto = new FacultyDto(1L, "FacultyName", "Color", new ArrayList<>());
        faculty = FacultyDto.toEntity(facultyDto);
        studentDto = new StudentDto(1L, "StudentName", 20);
        student = StudentDto.toEntity(studentDto);
        student.setFaculty(faculty);
    }

    @Test
    void createTest() {
        when(facultyServiceMock.findByName(anyString())).thenReturn(facultyDto);
        when(studentRepositoryMock.save(any(Student.class))).thenReturn(student);

        StudentDto result = out.create(studentDto, "FacultyName");

        assertNotNull(result);
        assertEquals(studentDto.getName(), result.getName());
        assertEquals(studentDto.getAge(), result.getAge());
        verify(studentRepositoryMock, times(1)).save(any(Student.class));

        assertThrows(ParameterIsNullException.class, () -> out.create(null, "FacultyName"));
        assertThrows(ParameterIsNullException.class, () -> out.create(studentDto, null));
    }

    @Test
    void createWithRandomFacultyTest() {
        when(facultyServiceMock.findRandom()).thenReturn(facultyDto);
        when(studentRepositoryMock.save(any(Student.class))).thenReturn(student);

        StudentDto result = out.createWithRandomFaculty(studentDto);

        assertNotNull(result);
        assertEquals(studentDto.getName(), result.getName());
        assertEquals(studentDto.getAge(), result.getAge());
        verify(studentRepositoryMock, times(1)).save(any(Student.class));

        assertThrows(ParameterIsNullException.class, () -> out.createWithRandomFaculty(null));
    }

    @Test
    void findByIdTest() {
        when(studentRepositoryMock.findById(anyLong())).thenReturn(Optional.ofNullable(student));

        StudentDto result = out.findById(1L);

        assertNotNull(result);
        assertEquals(studentDto.getName(), result.getName());
        assertEquals(studentDto.getAge(), result.getAge());
        verify(studentRepositoryMock, times(1)).findById(1L);

        when(studentRepositoryMock.findById(1L)).thenReturn(Optional.empty());
        assertThrows(StudentNotFoundException.class, () -> out.findById(1L));

        assertThrows(IncorrectIdException.class, () -> out.findById(-1L));
    }

    @Test
    void editTest() {
        when(studentRepositoryMock.save(any(Student.class))).thenReturn(student);
        when(studentRepositoryMock.findById(anyLong())).thenReturn(Optional.ofNullable(student));

        StudentDto result = out.edit(studentDto);

        assertNotNull(result);
        assertEquals(studentDto.getName(), result.getName());
        assertEquals(studentDto.getAge(), result.getAge());
        verify(studentRepositoryMock, times(1)).save(any(Student.class));

        assertThrows(ParameterIsNullException.class, () -> out.edit(null));
    }

    @Test
    void deleteTest() {
        when(studentRepositoryMock.findById(anyLong())).thenReturn(Optional.ofNullable(student));

        out.delete(1L);

        verify(studentRepositoryMock, times(1)).findById(anyLong());

        assertThrows(IncorrectIdException.class, () -> out.delete(-1L));
        assertThrows(IncorrectIdException.class, () -> out.delete(0L));
    }

    @Test
    void getAllTest() {
        when(studentRepositoryMock.findAll()).thenReturn(List.of(student));

        Collection<StudentDto> result = out.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(studentRepositoryMock, times(1)).findAll();
    }

    @Test
    void getAllByAgeTest() {
        when(studentRepositoryMock.findAllByAge(anyInt())).thenReturn(List.of(student));

        Collection<StudentDto> result = out.getAllByAge(20);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(studentRepositoryMock, times(1)).findAllByAge(anyInt());

        assertThrows(IncorrectAgeException.class, () -> out.getAllByAge(0));
        assertThrows(IncorrectAgeException.class, () -> out.getAllByAge(-1));
    }

    @Test
    void findByAgeBetweenTest() {
        when(studentRepositoryMock.findByAgeBetween(anyInt(), anyInt())).thenReturn(List.of(student));

        Collection<StudentDto> result = out.findByAgeBetween(5, 50);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(studentRepositoryMock, times(1)).findByAgeBetween(anyInt(), anyInt());

        assertThrows(IncorrectAgeException.class, () -> out.findByAgeBetween(0, 24));
        assertThrows(IncorrectAgeException.class, () -> out.findByAgeBetween(-1, 24));
        assertThrows(IncorrectAgeException.class, () -> out.findByAgeBetween(24, 0));
        assertThrows(IncorrectAgeException.class, () -> out.findByAgeBetween(24, -1));
    }

    @Test
    void findFacultyByStudentIdTest() {
        when(studentRepositoryMock.findById(anyLong())).thenReturn(Optional.ofNullable(student));

        FacultyDto result = out.findFacultyByStudentId(1L);

        assertNotNull(result);
        assertEquals(facultyDto.getName(), result.getName());
        assertEquals(facultyDto.getColor(), result.getColor());
        assertEquals(facultyDto.getStudents(), result.getStudents());

        assertThrows(IncorrectIdException.class, () -> out.findFacultyByStudentId(-1));
        assertThrows(IncorrectIdException.class, () -> out.findFacultyByStudentId(0));
    }

    @Test
    void findNamesStartsWithAAscUpperCaseTest() {
        Student studentNamedAaron = new Student();
        Student studentNamedAaron2 = new Student();
        Student studentNamedDik = new Student();
        Student studentNamedAlbert = new Student();
        Student studentNamedGennadiy = new Student();

        studentNamedAaron.setId(2L);
        studentNamedAaron.setName("Aaron");
        studentNamedAaron.setAge(22);
        studentNamedAaron.setFaculty(faculty);

        studentNamedAaron2.setId(3L);
        studentNamedAaron2.setName("Aaron");
        studentNamedAaron2.setAge(33);
        studentNamedAaron2.setFaculty(faculty);

        studentNamedDik.setId(4L);
        studentNamedDik.setName("Dik");
        studentNamedDik.setAge(44);
        studentNamedDik.setFaculty(faculty);

        studentNamedAlbert.setId(5L);
        studentNamedAlbert.setName("Albert");
        studentNamedAlbert.setAge(55);
        studentNamedAlbert.setFaculty(faculty);

        studentNamedGennadiy.setId(6L);
        studentNamedGennadiy.setName("Gennadiy");
        studentNamedGennadiy.setAge(66);
        studentNamedGennadiy.setFaculty(faculty);

        when(studentRepositoryMock.findAll()).thenReturn(List.of(
                student,
                studentNamedAaron,
                studentNamedAaron2,
                studentNamedDik,
                studentNamedAlbert,
                studentNamedGennadiy
        ));

        Collection<String> expected = List.of(
                studentNamedAaron.getName().toUpperCase(),
                studentNamedAlbert.getName().toUpperCase()
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
        Student studentNamedDik = new Student();
        Student studentNamedAlbert = new Student();

        studentNamedDik.setId(4L);
        studentNamedDik.setName("Dik");
        studentNamedDik.setAge(44);
        studentNamedDik.setFaculty(faculty);

        studentNamedAlbert.setId(5L);
        studentNamedAlbert.setName("Albert");
        studentNamedAlbert.setAge(55);
        studentNamedAlbert.setFaculty(faculty);

        when(studentRepositoryMock.findAll()).thenReturn(List.of(
                student,
                studentNamedDik,
                studentNamedAlbert
        ));

        Double avgAge = Stream.of(student, studentNamedAlbert, studentNamedDik)
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
}