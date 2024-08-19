package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.IncorrectIdException;
import ru.hogwarts.school.exception.ParameterIsNullException;
import ru.hogwarts.school.model.Faculty;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FacultyService {

    private final Map<Long, Faculty> faculties = new HashMap<>();
    private long idCounter = 0;

    public Faculty createFaculty(Faculty faculty) {
        notNullParameterChecker(faculty);
        faculty.setId(++idCounter);
        faculties.put(idCounter, faculty);
        return faculty;
    }

    public Faculty findFaculty(long facultyId) {
        idParameterChecker(facultyId);
        return faculties.get(facultyId);
    }

    public Faculty editFaculty(Faculty faculty) {
        notNullParameterChecker(faculty);
        if (faculties.containsKey(faculty.getId())) {
            faculties.put(faculty.getId(), faculty);
            return faculty;
        }
        return null;
    }

    public Faculty deleteFaculty(long facultyId) {
        idParameterChecker(facultyId);
        return faculties.remove(facultyId);
    }

    public Collection<Faculty> getAllFaculties() {
        return faculties.values();
    }

    public Collection<Faculty> getAllFacultiesByColor(String facultyColor) {
        notNullParameterChecker(facultyColor);
        return getAllFaculties()
                .stream()
                .filter(e -> e.getColor().equals(facultyColor))
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
}
