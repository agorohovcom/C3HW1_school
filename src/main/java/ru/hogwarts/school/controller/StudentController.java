package ru.hogwarts.school.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.dto.FacultyDto;
import ru.hogwarts.school.dto.StudentDto;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;

@RestController
@RequestMapping("student")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    @PostMapping("create_with_random_faculty")                          // POST http://localhost:8080/student/create
    public ResponseEntity<StudentDto> createWithRandomFaculty(@RequestBody StudentDto studentDto) {
        return new ResponseEntity<>(service.createWithRandomFaculty(studentDto), HttpStatus.OK);
    }

    @PostMapping("create_manually")         // POST http://localhost:8080/student/create_manually
    public ResponseEntity<StudentDto> createManually(
            @RequestParam String name,
            @RequestParam int age,
            @RequestParam String facultyName) {
        StudentDto studentDto = new StudentDto(null, name, age);
        return new ResponseEntity<>(service.create(studentDto, facultyName), HttpStatus.OK);
    }

    @GetMapping("{id}")                      // http://localhost:8080/student/1
    public ResponseEntity<StudentDto> get(@PathVariable(value = "id") long studentId) {
        StudentDto studentDto = service.findById(studentId);
        if (studentDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(studentDto);
    }

    @PutMapping                              // http://localhost:8080/student
    public ResponseEntity<StudentDto> edit(@RequestBody StudentDto studentDto) {
        StudentDto foundStudentDto = service.edit(studentDto);
        if (foundStudentDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(foundStudentDto);
    }

    @DeleteMapping("{id}")                   // http://localhost:8080/student/1
    public ResponseEntity<StudentDto> delete(@PathVariable(value = "id") long studentId) {
        service.delete(studentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping                             // http://localhost:8080/student
    public ResponseEntity<Collection<StudentDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("age/{age}")                // http://localhost:8080/student/age/18
    public ResponseEntity<Collection<StudentDto>> getAllByAge(@PathVariable(value = "age") int studentAge) {
        return ResponseEntity.ok(service.getAllByAge(studentAge));
    }

    @GetMapping("age")                      // http://localhost:8080/student/age
    public ResponseEntity<Collection<StudentDto>> findByAgeBetween(@RequestParam int min, @RequestParam int max) {
        return ResponseEntity.ok(service.findByAgeBetween(min, max));
    }

    @GetMapping("faculty")                  // http://localhost:8080/student/faculty
    public ResponseEntity<FacultyDto> getFacultyByStudentId(@RequestParam long studentId) {
        return ResponseEntity.ok(service.findFacultyByStudentId(studentId));
    }
}
