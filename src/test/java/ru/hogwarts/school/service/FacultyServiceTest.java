package ru.hogwarts.school.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hogwarts.school.dto.FacultyDto;
import ru.hogwarts.school.dto.StudentDto;
import ru.hogwarts.school.exception.IncorrectIdException;
import ru.hogwarts.school.exception.ParameterIsNullException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FacultyServiceTest {

    @InjectMocks
    private FacultyService out;

    @Mock
    private FacultyRepository facultyRepositoryMock;

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
//        faculty.setStudents(List.of(student));
    }

    @Test
    void createTest() {
        when(facultyRepositoryMock.save(any(Faculty.class))).thenReturn(faculty);

        FacultyDto result = out.create(facultyDto);

        assertNotNull(result);
        assertEquals(facultyDto.getName(), result.getName());
        assertEquals(facultyDto.getColor(), result.getColor());
        assertEquals(facultyDto.getStudents(), result.getStudents());
        verify(facultyRepositoryMock, times(1)).save(any(Faculty.class));

        assertThrows(ParameterIsNullException.class, () -> out.create(null));
    }

    @Test
    void findTest() {
        when(facultyRepositoryMock.findById(anyLong())).thenReturn(Optional.ofNullable(faculty));

        FacultyDto result = out.find(1L);

        assertNotNull(result);
        assertEquals(facultyDto.getName(), result.getName());
        assertEquals(facultyDto.getColor(), result.getColor());
        assertEquals(facultyDto.getStudents(), result.getStudents());
        verify(facultyRepositoryMock, times(1)).findById(anyLong());

        assertThrows(IncorrectIdException.class, () -> out.find(0));
        assertThrows(IncorrectIdException.class, () -> out.find(-1));
    }

    @Test
    void editTest() {
        when(facultyRepositoryMock.save(any(Faculty.class))).thenReturn(faculty);
        when(facultyRepositoryMock.findById(anyLong())).thenReturn(Optional.ofNullable(faculty));

        FacultyDto result = out.edit(facultyDto);

        assertNotNull(result);
        assertEquals(facultyDto.getName(), result.getName());
        assertEquals(facultyDto.getColor(), result.getColor());
        assertEquals(facultyDto.getStudents(), result.getStudents());
        verify(facultyRepositoryMock, times(1)).save(any(Faculty.class));

        assertThrows(ParameterIsNullException.class, () -> out.edit(null));
    }

    @Test
    void deleteTest() {
        when(facultyRepositoryMock.findById(anyLong())).thenReturn(Optional.ofNullable(faculty));

        out.delete(1L);

        verify(facultyRepositoryMock, times(1)).deleteById(anyLong());

        assertThrows(IncorrectIdException.class, () -> out.delete(0));
        assertThrows(IncorrectIdException.class, () -> out.delete(-1));
    }

    @Test
    void getAllTest() {
        when(facultyRepositoryMock.findAll()).thenReturn(List.of(faculty));

        Collection<FacultyDto> result = out.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(facultyRepositoryMock, times(1)).findAll();
    }

    @Test
    void getAllByColorTest() {
        when(facultyRepositoryMock.findAllByColorIgnoreCase(anyString())).thenReturn(List.of(faculty));

        Collection<FacultyDto> result = out.getAllByColor("Color");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(facultyRepositoryMock, times(1)).findAllByColorIgnoreCase(anyString());

        assertThrows(ParameterIsNullException.class, () -> out.getAllByColor(null));
    }

    @Test
    void getByNameOrColorIgnoreCaseTest() {
        when(facultyRepositoryMock.findByNameIgnoreCaseOrColorIgnoreCase(anyString(), anyString())).thenReturn(List.of(faculty));

        Collection<FacultyDto> result = out.getByNameOrColorIgnoreCase("FacultyName", "Color");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(facultyRepositoryMock, times(1)).findByNameIgnoreCaseOrColorIgnoreCase(anyString(), anyString());


        assertThrows(ParameterIsNullException.class, () -> out.getByNameOrColorIgnoreCase(null, "Color"));
        assertThrows(ParameterIsNullException.class, () -> out.getByNameOrColorIgnoreCase("FacultyName", null));
    }

    @Test
    void findByNameTest() {
        when(facultyRepositoryMock.findByNameIgnoreCase(anyString())).thenReturn(Optional.ofNullable(faculty));

        FacultyDto result = out.findByName("FacultyName");

        assertNotNull(result);
        assertEquals(facultyDto.getName(), result.getName());
        assertEquals(facultyDto.getColor(), result.getColor());
        assertEquals(facultyDto.getStudents(), result.getStudents());
        verify(facultyRepositoryMock, times(1)).findByNameIgnoreCase(anyString());

        assertThrows(ParameterIsNullException.class, () -> out.findByName(null));
    }

    @Test
    void findStudentsByFacultyNameTest() {
        when(facultyRepositoryMock.findByNameIgnoreCase(anyString())).thenReturn(Optional.ofNullable(faculty));

        Collection<StudentDto> result = out.findStudentsByFacultyName("FacultyName");

        assertNotNull(result);
        System.out.println(result.size());
        verify(facultyRepositoryMock, times(1)).findByNameIgnoreCase(anyString());

        assertThrows(ParameterIsNullException.class, () -> out.findStudentsByFacultyName(null));
    }

    @Test
    void findRandomTest() {
        when(facultyRepositoryMock.findRandom()).thenReturn(Optional.ofNullable(faculty));

        FacultyDto result = out.findRandom();

        assertNotNull(result);
        assertEquals(facultyDto.getName(), result.getName());
        assertEquals(facultyDto.getColor(), result.getColor());
        assertEquals(facultyDto.getStudents(), result.getStudents());
        verify(facultyRepositoryMock, times(1)).findRandom();
    }
}