package ru.hogwarts.school.dto;

import ru.hogwarts.school.model.Faculty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public class FacultyDto {

    private Long id;
    private String name;
    private String color;

    private Collection<StudentDto> students;

    public FacultyDto() {
    }

    public static FacultyDto toDto(Faculty faculty) {
        FacultyDto dto = new FacultyDto();
        dto.setId(faculty.getId());
        dto.setName(faculty.getName());
        dto.setColor(faculty.getColor());
        dto.setStudents(
                faculty.getStudents()
                        .stream()
                        .map(StudentDto::toDto)
                        .collect(Collectors.toCollection(ArrayList::new))
        );
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Collection<StudentDto> getStudents() {
        return students;
    }

    public void setStudents(Collection<StudentDto> students) {
        this.students = students;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FacultyDto that = (FacultyDto) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(color, that.color) && Objects.equals(students, that.students);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, students);
    }

    @Override
    public String toString() {
        return "FacultyDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", students=" + students +
                '}';
    }
}
