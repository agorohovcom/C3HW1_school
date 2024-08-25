package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.dto.FacultyDto;
import ru.hogwarts.school.dto.StudentDto;
import ru.hogwarts.school.exception.IncorrectAgeException;
import ru.hogwarts.school.exception.IncorrectIdException;
import ru.hogwarts.school.exception.ParameterIsNullException;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository repository;
    private final FacultyService facultyService;

    public StudentService(StudentRepository repository, FacultyService facultyService) {
        this.repository = repository;
        this.facultyService = facultyService;
    }

    public StudentDto create(StudentDto studentDto, String facultyName) {
        notNullParameterChecker(studentDto);
        notNullParameterChecker(facultyName);
        FacultyDto facultyDto = facultyService.findByName(facultyName);
        studentDto.setFacultyDto(facultyDto);
        return StudentDto.toDto(repository.save(StudentDto.toEntity(studentDto)));
    }

    public StudentDto createWithRandomFaculty(StudentDto studentDto) {
        notNullParameterChecker(studentDto);
        FacultyDto facultyDto = facultyService.findRandom();
        studentDto.setFacultyDto(facultyDto);
        return StudentDto.toDto(repository.save(StudentDto.toEntity(studentDto)));
    }

    public StudentDto findById(long studentId) {
        idParameterChecker(studentId);
        return repository
                .findById(studentId)
                .map(StudentDto::toDto)
                .orElseThrow(() -> new StudentNotFoundException("Студент с id " + studentId + " не найден"));
    }

    public StudentDto edit(StudentDto studentDto) {
        notNullParameterChecker(studentDto);
        findById(studentDto.getId()); // чтобы если с таким id нет, выдавало ошибку, а не создавало нового
        return StudentDto.toDto(repository.save(StudentDto.toEntity(studentDto)));
    }

    public void delete(long studentId) {
        idParameterChecker(studentId);
        findById(studentId); // чтобы если с таким id нет, выдавало ошибку, а не возвращало 200
        repository.deleteById(studentId);
    }

    public Collection<StudentDto> getAll() {
        return repository
                .findAll()
                .stream()
                .map(StudentDto::toDto)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Collection<StudentDto> getAllByAge(int studentAge) {
        ageParameterChecker(studentAge);
        return repository
                .findAllByAge(studentAge)
                .stream()
                .map(StudentDto::toDto)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Collection<StudentDto> findByAgeBetween(int min, int max) {
        ageParameterChecker(min);
        ageParameterChecker(max);
        return repository
                .findByAgeBetween(min, max)
                .stream()
                .map(StudentDto::toDto)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public FacultyDto findFacultyByStudentId(long studentId) {
        idParameterChecker(studentId);
        Student student = repository.findById(studentId).orElseThrow(() -> new StudentNotFoundException("Студент с id " + studentId + " не найден"));
        return FacultyDto.toDto(student.getFaculty());
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

    private void ageParameterChecker(int age) {
        if (age < 1) {
            throw new IncorrectAgeException("Возраст не может быть меньше 1");
        }
    }
}
