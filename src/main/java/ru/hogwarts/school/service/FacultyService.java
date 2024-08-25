package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.dto.FacultyDto;
import ru.hogwarts.school.exception.IncorrectIdException;
import ru.hogwarts.school.exception.ParameterIsNullException;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class FacultyService {

    private final FacultyRepository repository;

    public FacultyService(FacultyRepository repository) {
        this.repository = repository;
    }

    public FacultyDto createFaculty(FacultyDto facultyDto) {
        notNullParameterChecker(facultyDto);
        return FacultyDto.toDto(repository.save(FacultyDto.toNewEntity(facultyDto)));
    }

    public FacultyDto findFaculty(long facultyId) {
        idParameterChecker(facultyId);
        return repository
                .findById(facultyId)
                .map(FacultyDto::toDto)
                .orElse(null);
    }

    public FacultyDto editFaculty(FacultyDto facultyDto) {
        notNullParameterChecker(facultyDto);
        return FacultyDto.toDto(repository.save(FacultyDto.toEntity(facultyDto)));
    }

    public void deleteFaculty(long facultyId) {
        idParameterChecker(facultyId);
        repository.deleteById(facultyId);
    }

    public Collection<FacultyDto> getAllFaculties() {
        return repository
                .findAll()
                .stream()
                .map(FacultyDto::toDto)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Collection<FacultyDto> getAllFacultiesByColor(String facultyColor) {
        notNullParameterChecker(facultyColor);
        return repository.findAllByColor(facultyColor)
                .stream()
                .map(FacultyDto::toDto)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Collection<FacultyDto> getByNameOrColorIgnoreCase(String name, String color) {
        notNullParameterChecker(name);
        notNullParameterChecker(color);
        return repository.findByNameOrColorIgnoreCase(name, color)
                .stream()
                .map(FacultyDto::toDto)
                .collect(Collectors.toCollection(ArrayList::new));
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
