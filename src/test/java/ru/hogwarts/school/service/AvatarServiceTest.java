//package ru.hogwarts.school.service;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Value;
//import ru.hogwarts.school.dto.AvatarDto;
//import ru.hogwarts.school.dto.FacultyDto;
//import ru.hogwarts.school.dto.StudentDto;
//import ru.hogwarts.school.model.Avatar;
//import ru.hogwarts.school.model.Student;
//import ru.hogwarts.school.repository.AvatarRepository;
//
//import java.util.ArrayList;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class AvatarServiceTest {
//
//    @Value("${path.to.avatars.folder}")
//    private String folder;
//
//    @InjectMocks
//    private AvatarService out;
//
//    @Mock
//    private AvatarRepository repositoryMock;
//
//    @Mock
//    private StudentService studentServiceMock;
//
//    private StudentDto studentDto;
//    private AvatarDto avatarDto;
//    private Student student;
//    private Avatar avatar;
//
//    @BeforeEach
//    void setUp() {
//        studentDto = new StudentDto(1L, "StudentName", 20);
//        student = StudentDto.toEntity(studentDto);
//        student.setFaculty(
//                FacultyDto.toEntity(
//                        new FacultyDto(1L, "FacultyName", "Color", new ArrayList<>())));
//        avatarDto = new AvatarDto();
//        avatarDto.setId(1L);
//        avatar = AvatarDto.toEntity(avatarDto);
//    }
//
//    @Test
//    void findTest() {
//        when(repositoryMock.findById(anyLong())).thenReturn(Optional.of(avatar));
////        when(studentServiceMock.findById())
//
//        AvatarDto result = out.find(1L);
//    }
//
//    @Test
//    void findByStudentIdTest() {
//    }
//
//    @Test
//    void uploadTest() {
//    }
//}