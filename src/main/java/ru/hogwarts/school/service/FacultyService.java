package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.dto.FacultyDto;
import ru.hogwarts.school.dto.StudentDto;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.exception.IncorrectIdException;
import ru.hogwarts.school.exception.ParameterIsNullException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FacultyService {

    private final FacultyRepository repository;

    public FacultyService(FacultyRepository repository) {
        this.repository = repository;
    }

    public FacultyDto create(FacultyDto facultyDto) {
        notNullParameterChecker(facultyDto);
        return FacultyDto.toDto(repository.save(FacultyDto.toEntity(facultyDto)));
    }

    public FacultyDto find(long facultyId) {
        idParameterChecker(facultyId);
        return repository
                .findById(facultyId)
//                .findByIdWithStudents(facultyId)
                .map(FacultyDto::toDto)
                .orElseThrow(() -> new FacultyNotFoundException("Нет факультета с id \"" + facultyId + "\""));
    }

    public FacultyDto edit(FacultyDto facultyDto) {
        notNullParameterChecker(facultyDto);
        find(facultyDto.getId()); // чтобы если такого факультета не было, возвращалась ошибка, а не создавался новый
        return FacultyDto.toDto(repository.save(FacultyDto.toEntity(facultyDto)));
    }

    public void delete(long facultyId) {
        idParameterChecker(facultyId);
        find(facultyId); // чтобы если такого факультета не было, возвращалась ошибка, а не 200
        repository.deleteById(facultyId);
    }

    public Collection<FacultyDto> getAll() {
        return repository
                .findAll()
                .stream()
                .map(FacultyDto::toDto)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Collection<FacultyDto> getAllByColor(String facultyColor) {
        notNullParameterChecker(facultyColor);
        return repository
                .findAllByColorIgnoreCase(facultyColor)
                .stream()
                .map(FacultyDto::toDto)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Collection<FacultyDto> getByNameOrColorIgnoreCase(String name, String color) {
        notNullParameterChecker(name);
        notNullParameterChecker(color);
        return repository
                .findByNameIgnoreCaseOrColorIgnoreCase(name, color)
                .stream()
                .map(FacultyDto::toDto)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public FacultyDto findByName(String facultyName) {
        notNullParameterChecker(facultyName);
        return repository
                .findByNameIgnoreCase(facultyName)
                .map(FacultyDto::toDto)
                .orElseThrow(() -> new FacultyNotFoundException("Факультет с именем \"" + facultyName + "\" не найден"));
    }

    public Collection<StudentDto> findStudentsByFacultyName(String facultyName) {
        notNullParameterChecker(facultyName);
        FacultyDto facultyDto = findByName(facultyName);
        return facultyDto.getStudents();
    }

    public FacultyDto findRandom() {
        Optional<Faculty> faculty = repository.findRandom();
        if (faculty.isEmpty()) {
            throw new FacultyNotFoundException("Не найдено ни одного факультета");
        }
        return FacultyDto.toDto(faculty.get());
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
