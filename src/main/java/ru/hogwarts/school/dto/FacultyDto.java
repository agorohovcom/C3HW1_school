package ru.hogwarts.school.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@JsonIgnoreProperties(value = {"students"})
public class FacultyDto {

    private Long id;
    private String name;
    private String color;

    private Collection<StudentDto> students;

    public FacultyDto(Long id, String name, String color, Collection<StudentDto> students) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.students = students;
    }

    public FacultyDto() {
    }

    public static FacultyDto toDto(Faculty faculty) {
        FacultyDto dto = new FacultyDto();
        dto.setId(faculty.getId());
        dto.setName(faculty.getName());
        dto.setColor(faculty.getColor());
        if (faculty.getStudents() != null) {
            dto.setStudents(faculty
                    .getStudents()
                    .stream()
                    .map(StudentDto::toDto)
                    .collect(Collectors.toCollection(ArrayList::new))
            );
        } else {
            dto.setStudents(new ArrayList<StudentDto>());
        }
        return dto;
    }

    public static Faculty toEntity(FacultyDto dto) {
        Faculty faculty = new Faculty();
        faculty.setId(dto.getId());
        faculty.setName(dto.getName());
        faculty.setColor(dto.getColor());
        if (dto.getStudents() != null) {
            faculty.setStudents(dto
                    .getStudents()
                    .stream()
                    .map(StudentDto::toEntity)
                    .toList());
        } else {
            faculty.setStudents(new ArrayList<Student>());
        }
        return faculty;
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
        return students == null
                ? new ArrayList<>()
                : students;
    }

    public void setStudents(Collection<StudentDto> students) {
        this.students = students;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FacultyDto that = (FacultyDto) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(color, that.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
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
