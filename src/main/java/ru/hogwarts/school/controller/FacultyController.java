package ru.hogwarts.school.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.service.FacultyService;

import java.util.Collection;

@RestController
@RequestMapping("faculty")
public class FacultyController {

    private final FacultyService service;

    public FacultyController(FacultyService service) {
        this.service = service;
    }

    @PostMapping            // POST http://localhost:8080/faculty
    public Faculty createFaculty(Faculty faculty) {
        return service.createFaculty(faculty);
    }

    @GetMapping("{id}")     // http://localhost:8080/faculty/1
    public ResponseEntity<Faculty> getFaculty(@PathVariable(value = "id") long facultyId) {
        Faculty faculty = service.findFaculty(facultyId);
        if (faculty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculty);
    }

    @PutMapping             // http://localhost:8080/faculty
    public ResponseEntity<Faculty> editFaculty(@RequestBody Faculty faculty) {
        Faculty foundFaculty = service.editFaculty(faculty);
        if (foundFaculty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(foundFaculty);
    }

    @DeleteMapping("{id}")  // http://localhost:8080/faculty/1
    public Faculty deleteFaculty(@PathVariable(value = "id") long FacultyId) {
        return service.deleteFaculty(FacultyId);
    }

    @GetMapping             // http://localhost:8080/faculty
    public ResponseEntity<Collection<Faculty>> getAllFaculties() {
        return ResponseEntity.ok(service.getAllFaculties());
    }
}
