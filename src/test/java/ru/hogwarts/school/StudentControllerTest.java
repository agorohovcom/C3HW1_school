package ru.hogwarts.school;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.dto.StudentDto;

import java.util.Collection;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private StudentController studentController;

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void contextLoads() throws Exception {
        Assertions.assertThat(studentController).isNotNull();
    }

    @Test
    void createWithRandomFacultyTest() throws Exception {
        StudentDto studentDto = new StudentDto();
        studentDto.setName("Albert Test");
        studentDto.setAge(35);

        // Создаем заголовки запроса
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Создаем объект запроса
        HttpEntity<StudentDto> request = new HttpEntity<>(studentDto, headers);

        // Выполняем POST-запрос
        ResponseEntity<StudentDto> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/student/create_with_random_faculty",
                request,
                StudentDto.class
        );

        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(response.getBody().getName()).isEqualTo(studentDto.getName());
        Assertions.assertThat(response.getBody().getAge()).isEqualTo(studentDto.getAge());

        // Удаляем созданного студента из БД
        studentController.delete(response.getBody().getId());
    }

    // Это пример из шпаргалки. Вопросы:
    // Какая разница между postForEntity и postForObject?
    // В этом примере можно как-то удалить тестовые данные?
    // Какой способ лучше использовать?
//    @Test
//    public void createWithRandomFacultyTest() throws Exception {
//        StudentDto studentDto = new StudentDto();
//        studentDto.setName("Albert Test");
//        studentDto.setAge(35);
//
//        Assertions
//                .assertThat(restTemplate.postForObject(
//                        "http://localhost:" + port + "/student/create_with_random_faculty",
//                        studentDto,
//                        String.class))
//                .isNotNull();
//    }

    @Test
    void createTest() throws Exception {
        String name = "Dima Krasnik Test";
        int age = 54;
        String facultyName = "Grif";

        // Создаём объект запроса
        HttpEntity<Void> request = new HttpEntity<>(null);

        // Выполняем POST-запрос
        ResponseEntity<StudentDto> response = restTemplate.exchange(
                "http://localhost:" + port + "/student/create?name={name}&age={age}&facultyName={facultyName}",
                HttpMethod.POST,
                request,
                StudentDto.class,
                name, age, facultyName
        );

        // Проверяем результат
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getName()).isEqualTo(name);

        // Удаляем созданного студента из БД
        studentController.delete(response.getBody().getId());
    }

    @Test
    void getTest() throws Exception {
        // id существующего в БД студента
        long studentId = 2L;

        ResponseEntity<StudentDto> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/student/{id}",
                StudentDto.class,
                studentId);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getId()).isEqualTo(studentId);
    }

    @Test
    void editTest() throws Exception {
        // указываем id реально существующего в БД студента
        long id = 3L;
        String name = "Sveta Testovaya Podruga";
        int age = 38;

        StudentDto studentDto = new StudentDto();
        studentDto.setId(id);
        studentDto.setName(name);
        studentDto.setAge(age);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<StudentDto> requestEntity = new HttpEntity<>(studentDto, headers);

        // сохраняем реальную запись, чтобы восстановить её после теста
        StudentDto oldStudentDto = studentController.get(id).getBody();

        // Выполняем PUT-запрос
        ResponseEntity<StudentDto> response = restTemplate.exchange(
                "http://localhost:" + port + "/student",
                HttpMethod.PUT,
                requestEntity,
                StudentDto.class,
                studentDto.getId()
        );

        // Проверяем результат
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getName()).isEqualTo(name);

        // Возвращаем старую запись на место
        studentController.edit(oldStudentDto);
    }

    // вариант из шпаргалки
//    @Test
//    void getAllTest() throws Exception {
//        Assertions
//                .assertThat(restTemplate.getForObject("http://localhost:" + port + "/student", String.class))
//                .isNotNull();
//    }

    @Test
    void getAllTest() throws Exception {
        ResponseEntity<Collection<StudentDto>> response = restTemplate.exchange(
                "http://localhost:" + port + "/student",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<StudentDto>>() {
                }
        );

        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void deleteTest() throws Exception {
        // Добавляем студента для удаления
        StudentDto studentDto = new StudentDto();
        studentDto.setName("Zinka Test");
        studentDto.setAge(48);

        StudentDto addedStudentDto = studentController.createWithRandomFaculty(studentDto).getBody();
        long id = addedStudentDto.getId();

        HttpEntity<Void> request = new HttpEntity<>(null);

        // сохраняем сущность перед тестом удаления
        StudentDto oldStudentDto = studentController.get(id).getBody();

        ResponseEntity<StudentDto> response = restTemplate.exchange(
                "http://localhost:" + port + "/student/{id}",
                HttpMethod.DELETE,
                request,
                StudentDto.class,
                id
        );

        // Проверяем результат
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}