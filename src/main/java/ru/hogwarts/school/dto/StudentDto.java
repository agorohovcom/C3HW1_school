package ru.hogwarts.school.dto;

import ru.hogwarts.school.model.Student;

import java.util.Objects;

public class StudentDto {

    private Long id;
    private String name;
    private Integer age;

    public StudentDto() {
    }

    public static StudentDto toDto(Student student) {
        StudentDto studentDto = new StudentDto();
        studentDto.setId(student.getId());
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

    public static Student toNewEntity(StudentDto dto) {
        Student student = new Student();
        student.setId(null);
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentDto that = (StudentDto) o;
        return age == that.age && Objects.equals(id, that.id) && Objects.equals(name, that.name);
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
