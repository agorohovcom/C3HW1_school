package ru.hogwarts.school.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.hogwarts.school.exception.IncorrectIdException;
import ru.hogwarts.school.exception.ParameterIsNullException;
import ru.hogwarts.school.model.Faculty;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FacultyServiceTest {

    FacultyService out;

    @BeforeEach
    void setUp() {
        out = new FacultyService();
        out.createFaculty(new Faculty(0L, "Gryffindor", "red"));
        out.createFaculty(new Faculty(0L, "Slytherin", "green"));
        out.createFaculty(new Faculty(0L, "Ravenclaw", "blue"));
        out.createFaculty(new Faculty(0L, "Hufflepuff", "yellow"));
    }

    @Test
    void shouldCreateFacultyCorrectly() {
        Faculty expected1 = new Faculty(null, "Physteh", "purple");
        Faculty expected2 = new Faculty(333L, "Physmath", "brown");
        assertEquals(expected1, out.createFaculty(expected1));
        assertEquals(expected2, out.createFaculty(expected2));
        assertThrows(ParameterIsNullException.class, () -> out.createFaculty(null));
    }

    @Test
    void shouldFindFacultyCorrectly() {
        Faculty expected = new Faculty(1L, "Gryffindor", "red");
        assertEquals(expected, out.findFaculty(1L));
        assertNull(out.findFaculty(333L));
        assertThrows(IncorrectIdException.class, () -> out.findFaculty(-1));
    }

    @Test
    void shouldEditFacultyCorrectly() {
        Faculty expected = new Faculty(2L, "Mechmath", "pink");
        assertEquals(expected, out.editFaculty(expected));
        assertNull(out.editFaculty(new Faculty(222L, "Mechmath", "pink")));
        assertThrows(ParameterIsNullException.class, () -> out.editFaculty(null));
    }

    @Test
    void shouldDeleteFacultyCorrectly() {
        Faculty expected = new Faculty(3L, "Ravenclaw", "blue");
        int expectedSize = out.getAllFaculties().size() - 1;
        assertEquals(expected, out.deleteFaculty(3L));
        assertEquals(expectedSize, out.getAllFaculties().size());
        assertThrows(IncorrectIdException.class, () -> out.deleteFaculty(-1));
        assertNull(out.deleteFaculty(555L));
    }

    @Test
    void shouldGetAllFacultiesCorrectly() {
        Collection<Faculty> expected = List.of(
                new Faculty(1L, "Gryffindor", "red"),
                new Faculty(2L, "Slytherin", "green"),
                new Faculty(3L, "Ravenclaw", "blue"),
                new Faculty(4L, "Hufflepuff", "yellow")
        );
        assertIterableEquals(expected, out.getAllFaculties());
    }

    @Test
    void shouldGetAllFacultiesByColorCorrectly() {
        Collection<Faculty> expected = List.of(new Faculty(3L, "Ravenclaw", "blue"));
        assertIterableEquals(expected, out.getAllFacultiesByColor("blue"));
        assertIterableEquals(List.of(), out.getAllFacultiesByColor("krasniy"));
        assertThrows(ParameterIsNullException.class, () -> out.getAllFacultiesByColor(null));
    }
}