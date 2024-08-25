package ru.hogwarts.school.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping                    // POST http://localhost:8080/student
    public StudentDto createStudent(StudentDto studentDto) {
        return service.createStudent(studentDto);
    }

    @GetMapping("{id}")              // http://localhost:8080/student/1
    public ResponseEntity<StudentDto> getStudent(@PathVariable(value = "id") long studentId) {
        StudentDto studentDto = service.findStudent(studentId);
        if (studentDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(studentDto);
    }

    @PutMapping                      // http://localhost:8080/student
    public ResponseEntity<StudentDto> editStudent(@RequestBody StudentDto studentDto) {
        StudentDto foundStudentDto = service.editStudent(studentDto);
        if (foundStudentDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(foundStudentDto);
    }

    @DeleteMapping("{id}")          // http://localhost:8080/student/1
    public ResponseEntity<StudentDto> deleteStudent(@PathVariable(value = "id") long studentId) {
        service.deleteStudent(studentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping                     // http://localhost:8080/student
    public ResponseEntity<Collection<StudentDto>> getAllStudents() {
        return ResponseEntity.ok(service.getAllStudents());
    }

    @GetMapping("age/{age}")        // http://localhost:8080/student/age/18
    public ResponseEntity<Collection<StudentDto>> getAllStudentsByAge(@PathVariable(value = "age") int studentAge) {
        return ResponseEntity.ok(service.getAllStudentsByAge(studentAge));
    }

    @GetMapping("age")              // http://localhost:8080/student/age
    public ResponseEntity<Collection<StudentDto>> findByAgeBetween(@RequestParam int min, @RequestParam int max) {
        return ResponseEntity.ok(service.findByAgeBetween(min, max));
    }
}
