package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.IncorrectIdException;
import ru.hogwarts.school.exception.ParameterIsNullException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Collection;

@Service
public class FacultyService {

    private final FacultyRepository repository;

    public FacultyService(FacultyRepository repository) {
        this.repository = repository;
    }

    public Faculty createFaculty(Faculty faculty) {
        notNullParameterChecker(faculty);
        return repository.save(faculty);
    }

    public Faculty findFaculty(long facultyId) {
        idParameterChecker(facultyId);
        return repository.findById(facultyId).orElseGet(() -> null);
    }

    public Faculty editFaculty(Faculty faculty) {
        notNullParameterChecker(faculty);
        return repository.save(faculty);
    }

    public void deleteFaculty(long facultyId) {
        idParameterChecker(facultyId);
        repository.deleteById(facultyId);
    }

    public Collection<Faculty> getAllFaculties() {
        return repository.findAll();
    }

    public Collection<Faculty> getAllFacultiesByColor(String facultyColor) {
        notNullParameterChecker(facultyColor);
        return repository.findAllByColor(facultyColor);
    }

    public Collection<Faculty> getByNameOrColorIgnoreCase(String name, String color) {
        notNullParameterChecker(name);
        notNullParameterChecker(color);
        return repository.getByNameOrColorIgnoreCase(name, color);
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
