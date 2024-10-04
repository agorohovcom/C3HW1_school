package ru.hogwarts.school.constants;

import ru.hogwarts.school.dto.FacultyDto;
import ru.hogwarts.school.dto.StudentDto;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import java.util.ArrayList;

public class Constants {
    public static final FacultyDto FACULTY_DTO = new FacultyDto(1L, "FacultyName", "Color", new ArrayList<>());
    public static final Faculty FACULTY = FacultyDto.toEntity(FACULTY_DTO);

    public static final StudentDto STUDENT_DTO_1_SAM = new StudentDto(1L, "Sam", 20);
    public static final StudentDto STUDENT_DTO_2_AARON = new StudentDto(2L, "Aaron", 22);
    public static final StudentDto STUDENT_DTO_3_AARON = new StudentDto(3L, "Aaron", 33);
    public static final StudentDto STUDENT_DTO_4_DIK = new StudentDto(4L, "Dik", 44);
    public static final StudentDto STUDENT_DTO_5_ALBERT = new StudentDto(5L, "Albert", 55);
    public static final StudentDto STUDENT_DTO_6_GENNADIY = new StudentDto(6L, "Gennadiy", 66);

    public static final Student STUDENT_1_SAM = StudentDto.toEntity(STUDENT_DTO_1_SAM).setFaculty(FACULTY);
    public static final Student STUDENT_2_AARON = StudentDto.toEntity(STUDENT_DTO_2_AARON).setFaculty(FACULTY);
    public static final Student STUDENT_3_AARON = StudentDto.toEntity(STUDENT_DTO_3_AARON).setFaculty(FACULTY);
    public static final Student STUDENT_4_DIK = StudentDto.toEntity(STUDENT_DTO_4_DIK).setFaculty(FACULTY);
    public static final Student STUDENT_5_ALBERT = StudentDto.toEntity(STUDENT_DTO_5_ALBERT).setFaculty(FACULTY);
    public static final Student STUDENT_6_GENNADIY = StudentDto.toEntity(STUDENT_DTO_6_GENNADIY).setFaculty(FACULTY);


}
