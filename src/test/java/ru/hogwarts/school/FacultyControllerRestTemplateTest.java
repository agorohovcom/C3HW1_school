package ru.hogwarts.school;

import org.assertj.core.api.Assertions;
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
import ru.hogwarts.school.repository.FacultyRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FacultyControllerRestTemplateTest {

    @LocalServerPort
    private int port;

    @Autowired
    private FacultyController facultyController;

    @Autowired
    FacultyRepository facultyRepository;

    @Autowired
    TestRestTemplate restTemplate;

    private FacultyDto testFacultyDto;

    @BeforeEach
    void createTestEntity() {
        testFacultyDto = new FacultyDto();
        testFacultyDto.setName("Test faculty");
        testFacultyDto.setColor("Test color");

        // создаём факультет и присваиваем ему его же, но уже с id
        testFacultyDto = facultyController.createFaculty(testFacultyDto);
    }

    @AfterEach
    void deleteTestEntity() {
        // если есть, удаляем тестовый факультет из БД
        if (facultyRepository.existsById(testFacultyDto.getId())) {
            facultyRepository.deleteById(testFacultyDto.getId());
        }
    }

    @Test
    void contextLoads() {
        Assertions.assertThat(facultyController).isNotNull();
    }

    @Test
    void createFacultyTest() {
        String name = "Create Test Name";
        String color = "Create Test Color (green, of course)";

        FacultyDto facultyDto = new FacultyDto();
        facultyDto.setName(name);
        facultyDto.setColor(color);

        // Создаем заголовки запроса
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Создаем объект запроса
        HttpEntity<FacultyDto> request = new HttpEntity<>(facultyDto, headers);

        ResponseEntity<FacultyDto> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/faculty",
                request,
                FacultyDto.class
        );

        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody().getName()).isEqualTo(name);
        Assertions.assertThat(response.getBody().getColor()).isEqualTo(color);

        // удаляем из БД
        facultyRepository.deleteById(response.getBody().getId());
    }

    @Test
    void getFacultyTest() {
        long id = testFacultyDto.getId();

        ResponseEntity<FacultyDto> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/faculty/{id}",
                FacultyDto.class,
                id
        );

        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // удаляем тестовый факультет чтобы не было с таким id
        facultyController.deleteFaculty(id);

        response = restTemplate.getForEntity(
                "http://localhost:" + port + "/faculty/{id}",
                FacultyDto.class,
                id
        );

        Assertions.assertThat(response.getBody()).isNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void editFacultyTest() {
        String editedName = "Edited Test name";
        String editedColor = "Edited Test color";

        testFacultyDto.setName(editedName);
        testFacultyDto.setColor(editedColor);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<FacultyDto> request = new HttpEntity<>(testFacultyDto, headers);

        ResponseEntity<FacultyDto> response = restTemplate.exchange(
                "http://localhost:" + port + "/faculty",
                HttpMethod.PUT,
                request,
                FacultyDto.class
        );

        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody().getId()).isEqualTo(testFacultyDto.getId());
        Assertions.assertThat(response.getBody().getName()).isEqualTo(editedName);
        Assertions.assertThat(response.getBody().getColor()).isEqualTo(editedColor);

        //  удаляем тестовый факультет
        facultyRepository.deleteById(testFacultyDto.getId());
        request = new HttpEntity<>(testFacultyDto, headers);

        response = restTemplate.exchange(
                "http://localhost:" + port + "/faculty",
                HttpMethod.PUT,
                request,
                FacultyDto.class
        );

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteFacultyTest() {
        long id = testFacultyDto.getId();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + "/faculty/{id}",
                HttpMethod.DELETE,
                request,
                String.class,
                id
        );

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // пробуем удалить уже удалённый факультет
        response = restTemplate.exchange(
                "http://localhost:" + port + "/faculty/{id}",
                HttpMethod.DELETE,
                request,
                String.class,
                id
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
        String color = testFacultyDto.getColor();

        Assertions
                .assertThat(restTemplate.getForObject(
                        "http://localhost:" + port + "/faculty/color/{color}",
                        FacultyDto[].class,
                        color))
                .isNotNull();
    }

    @Test
    void getByNameOrColorIgnoreCaseTest() {
        String name = testFacultyDto.getName().toUpperCase();
        String color = testFacultyDto.getColor().toLowerCase();

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
        String facultyName = testFacultyDto.getName();

        Assertions
                .assertThat(restTemplate.getForObject(
                        "http://localhost:" + port + "/faculty/students?facultyName={facultyName}",
                        StudentDto[].class,
                        facultyName
                ))
                .isNotNull();
    }
}
