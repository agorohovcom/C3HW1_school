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
}