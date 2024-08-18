package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class FacultyService {

    private final Map<Long, Faculty> faculties = new HashMap<>();
    private long countId = 0;

    public Faculty createFaculty(Faculty faculty) {
        faculty.setId(++countId);
        faculties.put(countId, faculty);
        return faculty;
    }

    public Faculty findFaculty(long facultyId) {
        return faculties.get(facultyId);
    }

    public Faculty editFaculty(Faculty faculty) {
        if (faculties.containsKey(faculty.getId())) {
            faculties.put(faculty.getId(), faculty);
            return faculty;
        }
        return null;
    }

    public Faculty deleteFaculty(long facultyId) {
        return faculties.remove(facultyId);
    }

    public Collection<Faculty> getAllFaculties() {
        return faculties.values();
    }
}
