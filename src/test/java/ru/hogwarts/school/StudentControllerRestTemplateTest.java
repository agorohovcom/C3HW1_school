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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.dto.FacultyDto;
import ru.hogwarts.school.dto.StudentDto;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentControllerRestTemplateTest {

    @LocalServerPort
    private int port;

    @Autowired
    private StudentController studentController;

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    TestRestTemplate restTemplate;

    private StudentDto studentDto;
    private FacultyDto facultyDto;

    @BeforeEach
    void createTestData() {
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
    void deleteTestData() {
        studentRepository.deleteById(studentDto.getId());
        facultyRepository.deleteById(facultyDto.getId());
    }

    @Test
    void contextLoads() {
        Assertions.assertThat(studentController).isNotNull();
    }

    @Test
    void createWithRandomFacultyTest() throws Exception {
        // поля добавляемого студента
        String expectedName = "New student test name";
        int expectedAge = 54;

        JSONObject studentObject = new JSONObject();
        studentObject.put("name", expectedName);
        studentObject.put("age", expectedAge);

        // Создаем заголовки запроса
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Создаем объект запроса
        HttpEntity<String> request = new HttpEntity<>(studentObject.toString(), headers);

        // Выполняем POST-запрос
        ResponseEntity<StudentDto> response = restTemplate.exchange(
                "http://localhost:" + port + "/student/create_with_random_faculty",
                HttpMethod.POST,
                request,
                StudentDto.class
        );

        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(response.getBody().getName()).isEqualTo(expectedName);
        Assertions.assertThat(response.getBody().getAge()).isEqualTo(expectedAge);

        // Удаляем созданного студента из БД
        studentRepository.deleteById(response.getBody().getId());
    }

    @Test
    void createTest() throws Exception {
        String expectedName = "New student test name";
        int expectedAge = 54;
        String expectedFacultyName = facultyDto.getName();

        // Выполняем POST-запрос
        ResponseEntity<StudentDto> response = restTemplate.exchange(
                "http://localhost:"
                        + port
                        + "/student/create?name={expectedName}&age={expectedAge}&facultyName={expectedFacultyName}",
                HttpMethod.POST,
                null,
                StudentDto.class,
                expectedName, expectedAge, expectedFacultyName
        );

        // Проверяем результат
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getName()).isEqualTo(expectedName);
        Assertions.assertThat(response.getBody().getAge()).isEqualTo(expectedAge);

        // Удаляем созданного студента из БД
        studentRepository.deleteById(response.getBody().getId());
    }

    @Test
    void getTest() {
        ResponseEntity<StudentDto> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/student/{id}",
                StudentDto.class,
                studentDto.getId());

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getId()).isEqualTo(studentDto.getId());
        Assertions.assertThat(response.getBody().getName()).isEqualTo(studentDto.getName());
        Assertions.assertThat(response.getBody().getAge()).isEqualTo(studentDto.getAge());
        Assertions.assertThat(response.getBody().getFacultyDto()).isEqualTo(studentDto.getFacultyDto());
    }

    @Test
    void getFailIdTest() {
        studentRepository.deleteById(studentDto.getId());

        ResponseEntity<StudentDto> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/student/{id}",
                StudentDto.class,
                studentDto.getId());

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void editTest() throws Exception {
        String expectedName = studentDto.getName() + " Changed";
        int expectedAge = studentDto.getAge() + 30;

        JSONObject studentObject = new JSONObject();
        studentObject.put("id", studentDto.getId());
        studentObject.put("name", expectedName);
        studentObject.put("age", expectedAge);
        studentObject.put("facultyName", facultyDto.getName());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(studentObject.toString(), headers);

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
        Assertions.assertThat(response.getBody().getId()).isEqualTo(studentDto.getId());
        Assertions.assertThat(response.getBody().getName()).isEqualTo(expectedName);
        Assertions.assertThat(response.getBody().getAge()).isEqualTo(expectedAge);
    }

    @Test
    void editFailIdTest() throws Exception {
        // удаляем студента из БД
        studentRepository.deleteById(studentDto.getId());

        JSONObject studentObject = new JSONObject();
        studentObject.put("id", studentDto.getId());
        studentObject.put("name", studentDto.getName());
        studentObject.put("age", studentDto.getAge());
        studentObject.put("facultyName", facultyDto.getName());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(studentObject.toString(), headers);

        ResponseEntity<StudentDto> response = restTemplate.exchange(
                "http://localhost:" + port + "/student",
                HttpMethod.PUT,
                requestEntity,
                StudentDto.class,
                studentDto.getId()
        );

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteTest() throws Exception {
        // проверяем, что перед удалением студент есть
        Assertions.assertThat(studentRepository.existsById(studentDto.getId())).isTrue();

        ResponseEntity<StudentDto> response = restTemplate.exchange(
                "http://localhost:" + port + "/student/{id}",
                HttpMethod.DELETE,
                null,
                StudentDto.class,
                studentDto.getId()
        );

        // проверяем, что после удаления студента нет
        Assertions.assertThat(studentRepository.existsById(studentDto.getId())).isFalse();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void deleteFailIdTest() throws Exception {
        // удаляем студента из бд
        studentRepository.deleteById(studentDto.getId());

        ResponseEntity<StudentDto> response = restTemplate.exchange(
                "http://localhost:" + port + "/student/{id}",
                HttpMethod.DELETE,
                null,
                StudentDto.class,
                studentDto.getId()
        );

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getAllTest() {
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
    void getAllByAgeTest() {
        int age = 34;

        ResponseEntity<Collection<StudentDto>> response = restTemplate.exchange(
                "http://localhost:" + port + "/student/age/{age}",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<Collection<StudentDto>>() {
                },
                age);

        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void findByAgeBetweenTest() {
        int min = 5;
        int max = 35;

        ResponseEntity<Collection<StudentDto>> response = restTemplate.exchange(
                "http://localhost:" + port + "/student/age?min={min}&max={max}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<StudentDto>>() {
                },
                min, max);

        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void getFacultyByStudentIdTest() {
        long id = studentDto.getId();

        ResponseEntity<FacultyDto> response = restTemplate.exchange(
                "http://localhost:" + port + "/student/faculty?studentId={id}",
                HttpMethod.GET,
                null,
                FacultyDto.class,
                id
        );

        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // удаляем нашего студента из БД
        studentRepository.deleteById(studentDto.getId());

        response = restTemplate.exchange(
                "http://localhost:" + port + "/student/faculty?studentId={id}",
                HttpMethod.GET,
                null,
                FacultyDto.class,
                id
        );

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void countTest() {
        ResponseEntity<Long> response = restTemplate.exchange(
                "http://localhost:" + port + "/student/count",
                HttpMethod.GET,
                null,
                Long.class
        );

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void avgAgeTest() {
        ResponseEntity<Integer> response = restTemplate.exchange(
                "http://localhost:" + port + "/student/avg_age",
                HttpMethod.GET,
                null,
                Integer.class
        );

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void findFileLastStudents() {
        ResponseEntity<Collection<StudentDto>> response = restTemplate.exchange(
                "http://localhost:" + port + "/student/five_last_students",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<StudentDto>>() {
                }
        );

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(Objects.requireNonNull(response.getBody()).size()).isBetween(0, 5);
    }

    @Test
    void findNamesStartsWithAAscUpperCaseTest() {
        ResponseEntity<Collection<String>> response = restTemplate.exchange(
                "http://localhost:" + port + "/student/find_names_starts_with_A_asc_upper_case",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<String>>() {
                }
        );

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void getAvgAgeTest() {
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + "/student/get_avg_age",
                HttpMethod.GET,
                null,
                String.class
        );

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}