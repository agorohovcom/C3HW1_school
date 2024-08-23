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

    @PostMapping                    // POST http://localhost:8080/faculty
    public Faculty createFaculty(Faculty faculty) {
        return service.createFaculty(faculty);
    }

    @GetMapping("{id}")             // http://localhost:8080/faculty/1
    public ResponseEntity<Faculty> getFaculty(@PathVariable(value = "id") long facultyId) {
        Faculty faculty = service.findFaculty(facultyId);
        if (faculty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculty);
    }

    @PutMapping                     // http://localhost:8080/faculty
    public ResponseEntity<Faculty> editFaculty(@RequestBody Faculty faculty) {
        Faculty foundFaculty = service.editFaculty(faculty);
        if (foundFaculty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(foundFaculty);
    }

    @DeleteMapping("{id}")          // http://localhost:8080/faculty/1
    public ResponseEntity<Faculty> deleteFaculty(@PathVariable(value = "id") long facultyId) {
        service.deleteFaculty(facultyId);
        return ResponseEntity.ok().build();
    }

    @GetMapping                     // http://localhost:8080/faculty
    public ResponseEntity<Collection<Faculty>> getAllFaculties() {
        return ResponseEntity.ok(service.getAllFaculties());
    }

    @GetMapping("color/{color}")    // http://localhost:8080/student/color/red
    public ResponseEntity<Collection<Faculty>> getAllFacultiesByColor(@PathVariable(value = "color") String facultyColor) {
        return ResponseEntity.ok(service.getAllFacultiesByColor(facultyColor));
    }

    @GetMapping("search")
    public ResponseEntity<Collection<Faculty>> getByNameOrColorIgnoreCase(@RequestParam String name, @RequestParam String color) {
        return ResponseEntity.ok(service.getByNameOrColorIgnoreCase(name, color));
    }
}
