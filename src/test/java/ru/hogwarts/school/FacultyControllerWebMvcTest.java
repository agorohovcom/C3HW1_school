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
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.service.StudentService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FacultyController.class)
class FacultyControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacultyRepository facultyRepository;
    @MockBean
    private StudentRepository studentRepository;

    @SpyBean
    private FacultyService facultyService;
    @SpyBean
    private StudentService studentService;

    private Faculty faculty;

    @BeforeEach
    void createTestData() {
        long id = 1L;
        String facultyName = "Grif";
        String facultyColor = "red";

        faculty = new Faculty();
        faculty.setId(id);
        faculty.setName(facultyName);
        faculty.setColor(facultyColor);
    }

    @Test
    void createFacultyTest() throws Exception {
        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);

        JSONObject facultyObject = new JSONObject();
        facultyObject.put("id", faculty.getId());
        facultyObject.put("name", faculty.getName());
        facultyObject.put("color", faculty.getColor());

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/faculty")
                        .content(facultyObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(faculty.getId()))
                .andExpect(jsonPath("$.name").value(faculty.getName()))
                .andExpect(jsonPath("$.color").value(faculty.getColor()));
    }

    @Test
    void getFaculty() throws Exception {
        when(facultyRepository.findById(faculty.getId())).thenReturn(Optional.of(faculty));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(
                                "/faculty/{id}",
                                faculty.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(faculty.getId()))
                .andExpect(jsonPath("$.name").value(faculty.getName()))
                .andExpect(jsonPath("$.color").value(faculty.getColor()));

        // пытаемся получить несуществующий факультет
        when(facultyRepository.findById(faculty.getId())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .get(
                                "/faculty/{id}",
                                faculty.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void editFacultyTest() throws Exception {
        String editedName = faculty.getName() + " Edited";
        String editedColor = faculty.getColor() + " Edited";

        Faculty editedFaculty = new Faculty();
        editedFaculty.setId(faculty.getId());
        editedFaculty.setName(editedName);
        editedFaculty.setColor(editedColor);

        when(facultyRepository.findById(anyLong())).thenReturn(Optional.of(faculty));
        when(facultyRepository.save(any(Faculty.class))).thenReturn(editedFaculty);

        JSONObject facultyObject = new JSONObject();
        facultyObject.put("id", editedFaculty.getId());
        facultyObject.put("name", editedFaculty.getName());
        facultyObject.put("color", editedFaculty.getColor());

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/faculty")
                        .content(facultyObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(editedFaculty.getId()))
                .andExpect(jsonPath("$.name").value(editedFaculty.getName()))
                .andExpect(jsonPath("$.color").value(editedFaculty.getColor()));

        // пытаемся изменить несуществующий факультет
        when(facultyRepository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/faculty")
                        .content(facultyObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteFacultyTest() throws Exception {
        when(facultyRepository.findById(anyLong())).thenReturn(Optional.of(faculty));
        doNothing().when(facultyRepository).deleteById(anyLong());

        facultyRepository.deleteById(faculty.getId());

        verify(facultyRepository, times(1)).deleteById(anyLong());

        // тест удаления несуществующего факультета
        when(facultyRepository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete(
                                "/faculty/{id}",
                                faculty.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllFacultiesTest() throws Exception {
        when(facultyRepository.findAll()).thenReturn(List.of(faculty));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAllFacultiesByColorTest() throws Exception {
        when(facultyRepository.findAllByColorIgnoreCase(anyString())).thenReturn(List.of(faculty));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(
                                "/faculty/color/{color}",
                                faculty.getColor())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getByNameOrColorIgnoreCaseTest() throws Exception {
        when(facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(anyString(), anyString())).thenReturn(List.of(faculty));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(
                                "/faculty/search?name={name}&color={color}",
                                faculty.getName(), faculty.getColor())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void findStudentsByFacultyNameTest() throws Exception {
        when(facultyRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.of(faculty));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(
                                "/faculty/students?facultyName={facultyName}",
                                faculty.getName())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // тест получения студентов у несуществующего факультета
        when(facultyRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .get(
                                "/faculty/students?facultyName={facultyName}",
                                faculty.getName()))
                .andExpect(status().isNotFound());

    }
}