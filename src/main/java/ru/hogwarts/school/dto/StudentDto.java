package ru.hogwarts.school.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ru.hogwarts.school.model.Student;

import java.util.Objects;

@JsonIgnoreProperties(value = {"facultyDto"})
public class StudentDto {

    private Long id;
    private String name;
    private Integer age;

    private FacultyDto facultyDto;

    public StudentDto() {
    }

    public StudentDto(Long id, String name, Integer age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public static StudentDto toDto(Student student) {
        StudentDto studentDto = new StudentDto();
        if (studentDto.getId() != null) {
            studentDto.setId(student.getId());
        }
        studentDto.setAge(student.getAge());
        studentDto.setName(student.getName());
        return studentDto;
    }

    public static Student toEntity(StudentDto dto) {
        Student student = new Student();
        student.setId(dto.getId());
        student.setAge(dto.getAge());
        student.setName(dto.getName());
        return student;
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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public FacultyDto getFacultyDto() {
        return facultyDto;
    }

    public void setFacultyDto(FacultyDto facultyDto) {
        this.facultyDto = facultyDto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentDto that = (StudentDto) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(age, that.age);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, age);
    }

    @Override
    public String toString() {
        return "StudentDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
