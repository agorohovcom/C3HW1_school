//package ru.hogwarts.school.service;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import ru.hogwarts.school.dto.FacultyDto;
//import ru.hogwarts.school.exception.IncorrectIdException;
//import ru.hogwarts.school.exception.ParameterIsNullException;
//import ru.hogwarts.school.model.Faculty;
//import ru.hogwarts.school.repository.FacultyRepository;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//
//@ExtendWith(MockitoExtension.class)
//class FacultyServiceTest {
//
//    @InjectMocks
//    private FacultyService out;
//
//    @Mock
//    private FacultyRepository repositoryMock;
//
//    @Test
//    void shouldCreateFacultyCorrectly() {
////        FacultyDto expected = new FacultyDto(null, "Physteh", "purple", List.of());
//        Faculty faculty = new Faculty(1L, "drgerg", "sefgwerg");
////        out.create(expected);
//        verify(repositoryMock, times(1)).save(faculty);
//        assertThrows(ParameterIsNullException.class, () -> out.create(null));
//    }
//
//    @Test
//    void shouldFindFacultyCorrectly() {
//        Faculty expected = new Faculty(1L, "Gryffindor", "red");
//        out.findFaculty(1L);
//        verify(repositoryMock, times(1)).findById(1L);
//        assertThrows(IncorrectIdException.class, () -> out.findFaculty(-1));
//    }
//
//    @Test
//    void shouldEditFacultyCorrectly() {
//        Faculty expected = new Faculty(2L, "Mechmath", "pink");
//        out.editFaculty(expected);
//        verify(repositoryMock, times(1)).save(expected);
//        assertThrows(ParameterIsNullException.class, () -> out.editFaculty(null));
//    }
//
//    @Test
//    void shouldDeleteFacultyCorrectly() {
//        Faculty expected = new Faculty(3L, "Ravenclaw", "blue");
//        out.deleteFaculty(3L);
//        verify(repositoryMock, times(1)).deleteById(3L);
//        assertThrows(IncorrectIdException.class, () -> out.deleteFaculty(-1));
//    }
//
//    @Test
//    void shouldGetAllFacultiesCorrectly() {
//        out.getAllFaculties();
//        verify(repositoryMock, times(1)).findAll();
//    }
//
//    @Test
//    void shouldGetAllFacultiesByColorCorrectly() {
//        out.getAllFacultiesByColor("red");
//        verify(repositoryMock, times(1)).findAllByColor("red");
//        assertThrows(ParameterIsNullException.class, () -> out.getAllFacultiesByColor(null));
//    }
//}