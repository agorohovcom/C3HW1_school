package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FacultyService {

    Logger log = LoggerFactory.getLogger(FacultyService.class);

    private final FacultyRepository repository;

    public FacultyService(FacultyRepository repository) {
        this.repository = repository;
    }

    public FacultyDto create(FacultyDto facultyDto) {
        log.info("Method create called with parameters: {}", facultyDto);

        notNullParameterChecker(facultyDto);
        facultyDto.setId(null);
        FacultyDto result = FacultyDto.toDto(repository.save(FacultyDto.toEntity(facultyDto)));

        log.info("Method create completed with result: {}", result);
        return result;
    }

    public FacultyDto find(long facultyId) {
        log.info("Method find called with parameters: {}", facultyId);

        idParameterChecker(facultyId);
        FacultyDto result = repository
                .findById(facultyId)
                .map(FacultyDto::toDto)
                .orElseThrow(() -> new FacultyNotFoundException("No faculty with id \"" + facultyId + "\""));

        log.info("Method find completed with result: {}", result);
        return result;
    }

    public FacultyDto edit(FacultyDto facultyDto) {
        log.info("Method edit called with parameters: {}", facultyDto);

        notNullParameterChecker(facultyDto);
        find(facultyDto.getId()); // чтобы если такого факультета не было, возвращалась ошибка, а не создавался новый
        FacultyDto result = FacultyDto.toDto(repository.save(FacultyDto.toEntity(facultyDto)));

        log.info("Method edit completed with result: {}", result);
        return result;
    }

    public void delete(long facultyId) {
        log.info("Method delete called with parameters: {}", facultyId);

        idParameterChecker(facultyId);
        find(facultyId); // чтобы если такого факультета не было, возвращалась ошибка, а не 200
        repository.deleteById(facultyId);
        log.info("Method delete completed");
    }

    public Collection<FacultyDto> getAll() {
        log.info("Method getAll called");

        ArrayList<FacultyDto> result = repository
                .findAll()
                .stream()
                .map(FacultyDto::toDto)
                .collect(Collectors.toCollection(ArrayList::new));

        log.info("Method getAll completed with result size: {}", result.size());
        return result;
    }

    public Collection<FacultyDto> getAllByColor(String facultyColor) {
        log.info("Method getAllByColor called with parameters: {}", facultyColor);

        notNullParameterChecker(facultyColor);
        ArrayList<FacultyDto> result = repository
                .findAllByColorIgnoreCase(facultyColor)
                .stream()
                .map(FacultyDto::toDto)
                .collect(Collectors.toCollection(ArrayList::new));

        log.info("Method getAllByColor completed with result size: {}", result.size());
        return result;
    }

    public Collection<FacultyDto> getByNameOrColorIgnoreCase(String name, String color) {
        log.info("Method getByNameOrColorIgnoreCase called with parameters: {}, {}", name, color);

        notNullParameterChecker(name);
        notNullParameterChecker(color);
        ArrayList<FacultyDto> result = repository
                .findByNameIgnoreCaseOrColorIgnoreCase(name, color)
                .stream()
                .map(FacultyDto::toDto)
                .collect(Collectors.toCollection(ArrayList::new));

        log.info("Method getByNameOrColorIgnoreCase completed with result size: {}", result.size());
        return result;
    }

    public FacultyDto findByName(String facultyName) {
        log.info("Method findByName called with parameters: {}", facultyName);

        notNullParameterChecker(facultyName);
        FacultyDto result = repository
                .findByNameIgnoreCase(facultyName)
                .map(FacultyDto::toDto)
                .orElseThrow(() -> new FacultyNotFoundException("Факультет с именем \"" + facultyName + "\" не найден"));

        log.info("Method findByName completed with result: {}", result);
        return result;
    }

    public Collection<StudentDto> findStudentsByFacultyName(String facultyName) {
        log.info("Method findStudentsByFacultyName called with parameters: {}", facultyName);

        notNullParameterChecker(facultyName);
        FacultyDto facultyDto = findByName(facultyName);
        Collection<StudentDto> result = facultyDto.getStudents();

        log.info("Method findStudentsByFacultyName completed with result size: {}", result.size());
        return result;
    }

    // нет интеграционных тестов
    public String getLongestName() {
        log.info("Method getLongestName called");

        String result = repository
                .findAll()
                .stream()
                .map(Faculty::getName)
                .max(Comparator.comparingInt(String::length))
                .orElseThrow(() -> new FacultyNotFoundException("Faculty not found"));


        log.info("Method getLongestName completed with result: {}", result);
        return result;
    }

    public FacultyDto findRandom() {
        log.info("Method findRandom called");

        Optional<Faculty> faculty = repository.findRandom();
        if (faculty.isEmpty()) {
            throw new FacultyNotFoundException("No faculty found");
        }
        FacultyDto result = FacultyDto.toDto(faculty.get());

        log.info("Method findRandom completed with result: {}", result);
        return result;
    }

    private void notNullParameterChecker(Object o) {
        if (o == null) {
            throw new ParameterIsNullException("Parameter can't be null");
        }
    }

    private void idParameterChecker(long id) {
        if (id < 1) {
            throw new IncorrectIdException("ID can't be less than 1");
        }
    }
}
