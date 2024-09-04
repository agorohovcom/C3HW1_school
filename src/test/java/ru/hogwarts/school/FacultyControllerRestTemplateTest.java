package ru.hogwarts.school;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.dto.FacultyDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FacultyControllerRestTemplateTest {

    @LocalServerPort
    private int port;

    @Autowired
    private FacultyController facultyController;

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void contextLoads() throws Exception {
        Assertions.assertThat(facultyController).isNotNull();
    }

    @Test
    void createFacultyTest() throws Exception {
        String name = "Test Name";
        String color = "Test Color (green, of course)";

        FacultyDto facultyDto = new FacultyDto();
        facultyDto.setName(name);
        facultyDto.setColor(color);

        System.out.println("facultyDto сразу после создания: " + facultyDto);

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

        System.out.println("facultyDto из response: " + response.getBody());

        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody().getName()).isEqualTo(name);
        Assertions.assertThat(response.getBody().getColor()).isEqualTo(color);

        // удаляем из БД
        facultyController.deleteFaculty(response.getBody().getId());
    }
}
