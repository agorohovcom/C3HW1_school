package ru.hogwarts.school.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.dto.FacultyDto;
import ru.hogwarts.school.dto.StudentDto;
import ru.hogwarts.school.service.FacultyService;

import java.util.Collection;

@RestController
@RequestMapping("faculty")
public class FacultyController {

    private final FacultyService service;

    public FacultyController(FacultyService service) {
        this.service = service;
    }

    @PostMapping                    // http://localhost:8080/faculty
    public FacultyDto createFaculty(FacultyDto facultyDto) {
        return service.create(facultyDto);
    }

    @GetMapping("{id}")             // http://localhost:8080/faculty/1
    public ResponseEntity<FacultyDto> getFaculty(@PathVariable(value = "id") long facultyId) {
        FacultyDto faculty = service.find(facultyId);
        if (faculty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculty);
    }

    @PutMapping                     // http://localhost:8080/faculty
    public ResponseEntity<FacultyDto> editFaculty(@RequestBody FacultyDto facultyDto) {
        FacultyDto foundFacultyDto = service.edit(facultyDto);
        if (foundFacultyDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(foundFacultyDto);
    }

    @DeleteMapping("{id}")          // http://localhost:8080/faculty/1
    public ResponseEntity<FacultyDto> deleteFaculty(@PathVariable(value = "id") long facultyId) {
        service.delete(facultyId);
        return ResponseEntity.ok().build();
    }

    @GetMapping                     // http://localhost:8080/faculty
    public ResponseEntity<Collection<FacultyDto>> getAllFaculties() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("color/{color}")    // http://localhost:8080/student/color/red
    public ResponseEntity<Collection<FacultyDto>> getAllFacultiesByColor(@PathVariable(value = "color") String facultyColor) {
        return ResponseEntity.ok(service.getAllByColor(facultyColor));
    }

    @GetMapping("search")           // http://localhost:8080/student/search
    public ResponseEntity<Collection<FacultyDto>> getByNameOrColorIgnoreCase(@RequestParam String name, @RequestParam String color) {
        return ResponseEntity.ok(service.getByNameOrColorIgnoreCase(name, color));
    }

    @GetMapping("students")         // http://localhost:8080/student/students
    public ResponseEntity<Collection<StudentDto>> findStudentsByFacultyId(@RequestParam String facultyName) {
        return ResponseEntity.ok(service.findStudentsByFacultyName(facultyName));
    }
}
