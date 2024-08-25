package ru.hogwarts.school.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.dto.FacultyDto;
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
        return service.createFaculty(facultyDto);
    }

    @GetMapping("{id}")             // http://localhost:8080/faculty/1
    public ResponseEntity<FacultyDto> getFaculty(@PathVariable(value = "id") long facultyId) {
        FacultyDto faculty = service.findFaculty(facultyId);
        if (faculty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculty);
    }

    @PutMapping                     // http://localhost:8080/faculty
    public ResponseEntity<FacultyDto> editFaculty(@RequestBody FacultyDto facultyDto) {
        FacultyDto foundFacultyDto = service.editFaculty(facultyDto);
        if (foundFacultyDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(foundFacultyDto);
    }

    @DeleteMapping("{id}")          // http://localhost:8080/faculty/1
    public ResponseEntity<FacultyDto> deleteFaculty(@PathVariable(value = "id") long facultyId) {
        service.deleteFaculty(facultyId);
        return ResponseEntity.ok().build();
    }

    @GetMapping                     // http://localhost:8080/faculty
    public ResponseEntity<Collection<FacultyDto>> getAllFaculties() {
        return ResponseEntity.ok(service.getAllFaculties());
    }

    @GetMapping("color/{color}")    // http://localhost:8080/student/color/red
    public ResponseEntity<Collection<FacultyDto>> getAllFacultiesByColor(@PathVariable(value = "color") String facultyColor) {
        return ResponseEntity.ok(service.getAllFacultiesByColor(facultyColor));
    }

    @GetMapping("search")           // http://localhost:8080/student/search
    public ResponseEntity<Collection<FacultyDto>> getByNameOrColorIgnoreCase(@RequestParam String name, @RequestParam String color) {
        return ResponseEntity.ok(service.getByNameOrColorIgnoreCase(name, color));
    }
}
