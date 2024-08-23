package ru.hogwarts.school.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Student;
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
    public Student createStudent(Student student) {
        return service.createStudent(student);
    }

    @GetMapping("{id}")              // http://localhost:8080/student/1
    public ResponseEntity<Student> getStudent(@PathVariable(value = "id") long studentId) {
        Student student = service.findStudent(studentId);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student);
    }

    @PutMapping                      // http://localhost:8080/student
    public ResponseEntity<Student> editStudent(@RequestBody Student student) {
        Student foundStudent = service.editStudent(student);
        if (foundStudent == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(foundStudent);
    }

    @DeleteMapping("{id}")          // http://localhost:8080/student/1
    public ResponseEntity<Student> deleteStudent(@PathVariable(value = "id") long studentId) {
        service.deleteStudent(studentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping                     // http://localhost:8080/student
    public ResponseEntity<Collection<Student>> getAllStudents() {
        return ResponseEntity.ok(service.getAllStudents());
    }

    @GetMapping("age/{age}")        // http://localhost:8080/student/age/18
    public ResponseEntity<Collection<Student>> getAllStudentsByAge(@PathVariable(value = "age") int studentAge) {
        return ResponseEntity.ok(service.getAllStudentsByAge(studentAge));
    }

    @GetMapping("age")              // http://localhost:8080/student/age
    public ResponseEntity<Collection<Student>> findByAgeBetween(@RequestParam int min, @RequestParam int max) {
        return ResponseEntity.ok(service.findByAgeBetween(min, max));
    }
}
