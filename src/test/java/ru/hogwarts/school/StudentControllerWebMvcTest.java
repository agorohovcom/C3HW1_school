package ru.hogwarts.school;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.service.StudentService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = StudentController.class)
class StudentControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentRepository studentRepository;
    @MockBean
    private FacultyRepository facultyRepository;

    @SpyBean
    private StudentService service;
    @SpyBean
    private FacultyService facultyService;

    private Faculty faculty;
    private Student student;

    @BeforeEach
    void createTestData() {
        long id = 1L;
        String name = "Gena";
        int age = 34;
        String facultyName = "Grif";
        String facultyColor = "red";

        faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName(facultyName);
        faculty.setColor(facultyColor);

        student = new Student();
        student.setId(id);
        student.setName(name);
        student.setAge(age);
        student.setFaculty(faculty);
    }

    @Test
    void createWithRandomFacultyTest() throws Exception {
        when(facultyRepository.findRandom()).thenReturn(Optional.of(faculty));
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        JSONObject studentObject = new JSONObject();
        studentObject.put("id", student.getId());
        studentObject.put("name", student.getName());
        studentObject.put("age", student.getAge());

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/student/create_with_random_faculty")
                        .content(studentObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(student.getId()))
                .andExpect(jsonPath("$.name").value(student.getName()))
                .andExpect(jsonPath("$.age").value(student.getAge()));
    }

    @Test
    void createTest() throws Exception {
        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(facultyRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.of(faculty));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/student/create")
                        .param("name", student.getName())
                        .param("age", String.valueOf(student.getAge()))
                        .param("facultyName", faculty.getName())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(student.getId()))
                .andExpect(jsonPath("$.name").value(student.getName()))
                .andExpect(jsonPath("$.age").value(student.getAge()));
    }

    @Test
    void getTest() throws Exception {
        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/{id}", student.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(student.getId()))
                .andExpect(jsonPath("$.name").value(student.getName()))
                .andExpect(jsonPath("$.age").value(student.getAge()));

        // тест получения несуществующего студента
        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/{id}", student.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void editTest() throws Exception {
        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        String editedName = "Edited Name";
        int editedAge = student.getAge() + 100;

        JSONObject studentObject = new JSONObject();
        studentObject.put("id", student.getId());
        studentObject.put("name", editedName);
        studentObject.put("age", editedAge);

        mockMvc.perform((MockMvcRequestBuilders
                        .put("/student"))
                        .content(studentObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(student.getId()))
                .andExpect(jsonPath("$.name").value(editedName))
                .andExpect(jsonPath("$.age").value(editedAge));

        // тест редактирования несуществующего студента
        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform((MockMvcRequestBuilders
                        .put("/student"))
                        .content(studentObject.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTest() throws Exception {
        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));
        doNothing().when(studentRepository).deleteById(anyLong());

        studentRepository.deleteById(student.getId());

        verify(studentRepository).deleteById(student.getId());

        // тест удаления несуществующего студента
        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/student/{id}", student.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllTest() throws Exception {
        when(studentRepository.findAll()).thenReturn(List.of(student));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAllByAgeTest() throws Exception {
        when(studentRepository.findAllByAge(anyInt())).thenReturn(List.of(student));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(
                                "/student/age/{age}",
                                student.getAge())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void findByAgeBetweenTest() throws Exception {
        when(studentRepository.findByAgeBetween(anyInt(), anyInt())).thenReturn(List.of(student));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(
                                "/student/age?min={min}&max={max}",
                                student.getAge() - 1, student.getAge() + 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getFacultyByStudentId() throws Exception {
        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(
                                "/student/faculty?studentId={studentId}",
                                student.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(faculty.getId()))
                .andExpect(jsonPath("$.name").value(faculty.getName()))
                .andExpect(jsonPath("$.color").value(faculty.getColor()));

        // тест получения факультета несуществующего студента
        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .get(
                                "/student/faculty?studentId={studentId}",
                                student.getId() + 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void countTest() throws Exception {
        when(studentRepository.count()).thenReturn(1L);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/count")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("1"));

        // тест получения количества студентов из пустой таблицы
        when(studentRepository.count()).thenReturn(0L);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/count")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("0"));
        ;

    }

    @Test
    void avgAgeTest() throws Exception {
        when(studentRepository.avgAge()).thenReturn(student.getAge());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/avg_age")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(student.getAge().toString()));
    }

    @Test
    void findFileLastStudentsTest() throws Exception {
        when(studentRepository.findFileLastStudents()).thenReturn(List.of(student));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/five_last_students")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}