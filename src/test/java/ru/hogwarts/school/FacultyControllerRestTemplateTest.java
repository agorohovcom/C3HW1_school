package ru.hogwarts.school;

import org.assertj.core.api.Assertions;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.dto.FacultyDto;
import ru.hogwarts.school.dto.StudentDto;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FacultyControllerRestTemplateTest {

    @LocalServerPort
    private int port;

    @Autowired
    private FacultyController facultyController;

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    TestRestTemplate restTemplate;

    private StudentDto studentDto;
    private FacultyDto facultyDto;

    @BeforeEach
    void createTestEntity() {
        String studentName = "Albert Test Buddy";
        int studentAge = 35;

        String facultyName = "Super Mega Test Faculty";
        String facultyColor = "Pink";

        Student student = new Student(null, studentName, studentAge);
        Faculty faculty = new Faculty(null, facultyName, facultyColor);
        student.setFaculty(faculty);
        faculty.setStudents(List.of(student));

        facultyDto = FacultyDto.toDto(facultyRepository.save(faculty));
        studentDto = StudentDto.toDto(studentRepository.save(student));
    }

    @AfterEach
    void deleteTestEntity() {
        // настроил "cascade = CascadeType.REMOVE", можно удалять только факультет, но оставлю так
        studentRepository.deleteById(studentDto.getId());
        facultyRepository.deleteById(facultyDto.getId());
    }

    @Test
    void contextLoads() {
        Assertions.assertThat(facultyController).isNotNull();
    }

    @Test
    void createFacultyTest() throws Exception {
        String expectedName = "Create Test Name";
        String expectedColor = "Create Test Color (green, of course)";

        JSONObject facultyObject = new JSONObject();
        facultyObject.put("name", expectedName);
        facultyObject.put("color", expectedColor);

        // Создаем заголовки запроса
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Создаем объект запроса
        HttpEntity<String> request = new HttpEntity<>(facultyObject.toString(), headers);

        ResponseEntity<FacultyDto> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/faculty",
                request,
                FacultyDto.class
        );

        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody().getName()).isEqualTo(expectedName);
        Assertions.assertThat(response.getBody().getColor()).isEqualTo(expectedColor);

        // удаляем из БД
        facultyRepository.deleteById(response.getBody().getId());
    }

    @Test
    void getFacultyTest() {
        ResponseEntity<FacultyDto> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/faculty/{id}",
                FacultyDto.class,
                facultyDto.getId()
        );

        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody().getId()).isEqualTo(facultyDto.getId());
        Assertions.assertThat(response.getBody().getName()).isEqualTo(facultyDto.getName());
        Assertions.assertThat(response.getBody().getColor()).isEqualTo(facultyDto.getColor());
    }

    @Test
    void getFacultyFailIdTest() {
        deleteTestEntity();

        ResponseEntity<FacultyDto> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/faculty/{id}",
                FacultyDto.class,
                facultyDto.getId()
        );

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void editFacultyTest() throws Exception {
        String editedName = facultyDto.getName() + " Edited";
        String editedColor = facultyDto.getColor() + " Edited";

        JSONObject facultyObject = new JSONObject();
        facultyObject.put("id", facultyDto.getId());
        facultyObject.put("name", editedName);
        facultyObject.put("color", editedColor);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(facultyObject.toString(), headers);

        ResponseEntity<FacultyDto> response = restTemplate.exchange(
                "http://localhost:" + port + "/faculty",
                HttpMethod.PUT,
                request,
                FacultyDto.class
        );

        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody().getId()).isEqualTo(facultyDto.getId());
        Assertions.assertThat(response.getBody().getName()).isEqualTo(editedName);
        Assertions.assertThat(response.getBody().getColor()).isEqualTo(editedColor);
    }

    @Test
    void editFacultyFailIdTest() throws Exception {
        deleteTestEntity();

        JSONObject facultyObject = new JSONObject();
        facultyObject.put("id", facultyDto.getId());
        facultyObject.put("name", facultyDto.getName());
        facultyObject.put("color", facultyDto.getColor());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(facultyObject.toString(), headers);

        ResponseEntity<FacultyDto> response = restTemplate.exchange(
                "http://localhost:" + port + "/faculty",
                HttpMethod.PUT,
                request,
                FacultyDto.class
        );

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteFacultyTest() {
        // проверяем, что перед удалением факультет есть
        Assertions.assertThat(facultyRepository.existsById(facultyDto.getId())).isTrue();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + "/faculty/{id}",
                HttpMethod.DELETE,
                request,
                String.class,
                facultyDto.getId()
        );

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // проверяем, что после удаления факультета нет
        Assertions.assertThat(facultyRepository.existsById(facultyDto.getId())).isFalse();
    }

    @Test
    void deleteFacultyFailIdTest() {
        // удаляем факультет
        deleteTestEntity();
        // проверяем, что факультета нет
        Assertions.assertThat(facultyRepository.existsById(facultyDto.getId())).isFalse();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + "/faculty/{id}",
                HttpMethod.DELETE,
                request,
                String.class,
                facultyDto.getId()
        );

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }


    @Test
    void getAllFacultiesTest() {
        Assertions
                .assertThat(restTemplate.getForObject(
                        "http://localhost:" + port + "/faculty",
//                        String.class))         // работает и со String
//                        Collection.class))     // и так
                        FacultyDto[].class))    // и так
                .isNotNull();
    }

    @Test
    void getAllFacultiesByColorTest() {
        String color = facultyDto.getColor();

        Assertions
                .assertThat(restTemplate.getForObject(
                        "http://localhost:" + port + "/faculty/color/{color}",
                        FacultyDto[].class,
                        color))
                .isNotNull();
    }

    @Test
    void getByNameOrColorIgnoreCaseTest() {
        String name = facultyDto.getName().toUpperCase();
        String color = facultyDto.getColor().toLowerCase();

        Assertions
                .assertThat(restTemplate.getForObject(
                        "http://localhost:" + port + "/faculty/search?name={name}&color={color}",
                        FacultyDto[].class,
                        name, color
                ))
                .isNotNull();
    }

    @Test
    void findStudentsByFacultyNameTest() {
        String facultyName = facultyDto.getName();

        Assertions
                .assertThat(restTemplate.getForObject(
                        "http://localhost:" + port + "/faculty/students?facultyName={facultyName}",
                        StudentDto[].class,
                        facultyName
                ))
                .isNotNull();
    }
}
