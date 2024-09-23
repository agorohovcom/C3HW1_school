package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.dto.FacultyDto;
import ru.hogwarts.school.dto.StudentDto;
import ru.hogwarts.school.exception.IncorrectAgeException;
import ru.hogwarts.school.exception.IncorrectIdException;
import ru.hogwarts.school.exception.ParameterIsNullException;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

    Logger log = LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository repository;
    private final FacultyService facultyService;

    public StudentService(StudentRepository repository, FacultyService facultyService) {
        this.repository = repository;
        this.facultyService = facultyService;
    }

    public StudentDto create(StudentDto studentDto, String facultyName) {
        log.info("Method create called with parameters: {}, {}", studentDto, facultyName);

        notNullParameterChecker(studentDto);
        notNullParameterChecker(facultyName);

        FacultyDto facultyDto = facultyService.findByName(facultyName);
        Faculty faculty = FacultyDto.toEntity(facultyDto);
        Student student = StudentDto.toEntity(studentDto);
        student.setFaculty(faculty);

        StudentDto result = StudentDto.toDto(repository.save(student));

        log.info("Method create completed with result: {}", result);
        return result;
    }

    public StudentDto createWithRandomFaculty(StudentDto studentDto) {
        log.info("Method createWithRandomFaculty called with parameters: {}", studentDto);

        notNullParameterChecker(studentDto);
        FacultyDto facultyDto = facultyService.findRandom();
        Faculty faculty = FacultyDto.toEntity(facultyDto);
        Student student = StudentDto.toEntity(studentDto);
        student.setFaculty(faculty);

        StudentDto result = StudentDto.toDto(repository.save(student));

        log.info("Method createWithRandomFaculty completed with result: {}", result);
        return result;
    }

    public StudentDto findById(long studentId) {
        log.info("Method findById called with parameters: {}", studentId);

        idParameterChecker(studentId);
        StudentDto result = repository
                .findById(studentId)
                .map(StudentDto::toDto)
                .orElseThrow(() -> new StudentNotFoundException("Student with id " + studentId + " not found"));

        log.info("Method findById completed with result: {}", result);
        return result;
    }

    public StudentDto edit(StudentDto studentDto) {
        log.info("Method edit called with parameters: {}", studentDto);

        notNullParameterChecker(studentDto);
        findById(studentDto.getId()); // чтобы если с таким id нет, выдавало ошибку, а не создавало нового
        Student student = StudentDto.toEntity(studentDto);
        student.setFaculty(FacultyDto.toEntity(findFacultyByStudentId(studentDto.getId())));
        repository.save(student);

        log.info("Method edit completed with result: {}", studentDto);
        return studentDto;
    }

    public void delete(long studentId) {
        log.info("Method delete called with parameters: {}", studentId);

        idParameterChecker(studentId);
        findById(studentId); // чтобы если с таким id нет, выдавало ошибку, а не возвращало 200
        repository.deleteById(studentId);

        log.info("Method delete completed");
    }

    public Collection<StudentDto> getAll() {
        log.info("Method getAll called");

        ArrayList<StudentDto> result = repository
                .findAll()
                .stream()
                .map(StudentDto::toDto)
                .collect(Collectors.toCollection(ArrayList::new));

        log.info("Method getAll completed with result size: {}", result.size());
        return result;
    }

    public Collection<StudentDto> getAllByAge(int studentAge) {
        log.info("Method getAllByAge called with parameters: {}", studentAge);

        ageParameterChecker(studentAge);
        ArrayList<StudentDto> result = repository
                .findAllByAge(studentAge)
                .stream()
                .map(StudentDto::toDto)
                .collect(Collectors.toCollection(ArrayList::new));

        log.info("Method getAllByAge completed with result size: {}", result.size());
        return result;
    }

    public Collection<StudentDto> findByAgeBetween(int min, int max) {
        log.info("Method findByAgeBetween called with parameters: {}, {}", min, max);

        ageParameterChecker(min);
        ageParameterChecker(max);
        ArrayList<StudentDto> result = repository
                .findByAgeBetween(min, max)
                .stream()
                .map(StudentDto::toDto)
                .collect(Collectors.toCollection(ArrayList::new));

        log.info("Method findByAgeBetween completed with result size: {}", result.size());
        return result;
    }

    public FacultyDto findFacultyByStudentId(long studentId) {
        log.info("Method findFacultyByStudentId called with parameters: {}", studentId);

        idParameterChecker(studentId);
        Student student = repository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Студент с id " + studentId + " не найден"));
        FacultyDto result = FacultyDto.toDto(student.getFaculty());

        log.info("Method findFacultyByStudentId completed with result: {}", result);
        return result;
    }

    public long count() {
        log.info("Method count called");

        long result = repository.count();

        log.info("Method count completed with result: {}", result);
        return result;
    }

    public int avgAge() {
        log.info("Method avgAge called");

        int result = repository.avgAge();

        log.info("Method avgAge completed with result: {}", result);
        return result;
    }

    public Collection<StudentDto> findFileLastStudents() {
        log.info("Method findFileLastStudents called");

        ArrayList<StudentDto> result = repository
                .findFileLastStudents()
                .stream()
                .map(StudentDto::toDto)
                .collect(Collectors.toCollection(ArrayList::new));

        log.info("Method findFileLastStudents completed with result size: {}", result.size());
        return result;
    }

    // нет интеграционных тестов
    public Collection<String> findNamesStartsWithAAscUpperCase() {
        log.info("Method findNamesStartsWithAAsc called");

        List<String> result = repository
                .findAll()
                .stream()
                .filter(s -> s.getName().startsWith("A"))
                .map(s -> s.getName().toUpperCase())
                .sorted()
                .distinct()
                .toList();

        log.info("Method findNamesStartsWithAAsc completed with result size: {}", result.size());
        return result;
    }

    // нет тестов
    public String getAvgAge() {
        log.info("Method getAvgAge called");

        Double avgAge = repository
                .findAll()
                .stream()
                .mapToDouble(Student::getAge)
                .average()
                .orElse(0.0);

        String result = String.format("%.2f", avgAge);

        log.info("Method getAvgAge completed with result: {}", result);
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

    private void ageParameterChecker(int age) {
        if (age < 1) {
            throw new IncorrectAgeException("Age can't be less than 1");
        }
    }
}
