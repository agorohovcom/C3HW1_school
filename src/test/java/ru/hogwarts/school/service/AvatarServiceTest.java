package ru.hogwarts.school.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.dto.AvatarDto;
import ru.hogwarts.school.dto.StudentDto;
import ru.hogwarts.school.exception.AvatarNotFoundException;
import ru.hogwarts.school.exception.IncorrectIdException;
import ru.hogwarts.school.exception.ParameterIsNullException;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.repository.AvatarRepository;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvatarServiceTest {

    @InjectMocks
    private AvatarService out;

    @Mock
    private AvatarRepository avatarRepositoryMock;
    @Mock
    private StudentService studentServiceMock;

    private MultipartFile avatarFileMock;

    private String folder;
    private String testFolderPath = "./test_avatar_folder/";

    private StudentDto studentDto;
    private AvatarDto avatarDto;
    private Avatar avatar;

    @BeforeEach
    void setUp() throws Exception {
        // задаём поле folder и создаём папку для тестов
        Field folderField = AvatarService.class.getDeclaredField("folder");
        folderField.setAccessible(true);
        folderField.set(out, testFolderPath);

        folder = (String) folderField.get(out);

        File directory = new File(folder);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // тестовые данные
        studentDto = new StudentDto();
        studentDto.setId(1L);
        studentDto.setName("Test student name");
        studentDto.setAge(34);

        avatarDto = new AvatarDto();
        avatarDto.setId(1L);
        avatarDto.setData(new byte[]{1, 2, 10});
        avatarDto.setStudentDto(studentDto);

        avatar = AvatarDto.toEntity(avatarDto);

        // тестовый файл
        File testFile = new File("./src/test/java/ru/hogwarts/school/test_files/test_file.jpg");
        FileInputStream fis = new FileInputStream(testFile);

        avatarFileMock = new MockMultipartFile(
                "avatar",
                "avatar.png",
                "image/png",
                fis
        );
    }

    @AfterEach
    void tearDown() {
        // удаляем тестовую папку
        File directory = new File(testFolderPath);
        if (directory.exists()) {
            deleteTestDirectory(directory);
        }
    }

    private void deleteTestDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteTestDirectory(file);
                } else {
                    file.delete();
                }
            }
            directory.delete();
        }
    }

    @Test
    void findTest() {
        when(avatarRepositoryMock.findById(avatarDto.getId())).thenReturn(Optional.of(avatar));

        AvatarDto actual = out.find(1L);

        assertNotNull(actual);
        assertEquals(avatarDto, actual);
    }

    @Test
    void findIdNotExistTest() {
        when(avatarRepositoryMock.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(AvatarNotFoundException.class, () -> out.find(avatarDto.getId()));
    }

    @Test
    void findIncorrectIdTest() {
        assertThrows(IncorrectIdException.class, () -> out.find(0));
        assertThrows(IncorrectIdException.class, () -> out.find(-1));
    }

    @Test
    void findByStudentIdTest() {
        when(avatarRepositoryMock.findByStudentId(anyLong())).thenReturn(Optional.of(avatar));

        AvatarDto actual = out.findByStudentId(1L);

        assertNotNull(actual);
        assertEquals(avatarDto, actual);
    }

    @Test
    void findByStudentIdNotExistTest() {
        when(avatarRepositoryMock.findByStudentId(anyLong())).thenReturn(Optional.of(new Avatar()));

        AvatarDto actual = out.findByStudentId(1L);

        assertNotNull(actual);
        assertEquals(new AvatarDto(), actual);
    }

    @Test
    void findByIncorrectStudentIdTest() {
        assertThrows(IncorrectIdException.class, () -> out.findByStudentId(0));
        assertThrows(IncorrectIdException.class, () -> out.findByStudentId(-1));
    }

    @Test
    void uploadTest() throws Exception {
        when(studentServiceMock.findById(anyLong())).thenReturn(studentDto);
        when(avatarRepositoryMock.findByStudentId(anyLong())).thenReturn(Optional.of(avatar));

        out.upload(studentDto.getId(), avatarFileMock);

        // Проверка, что файл был создан
        Path filePath = Path.of(testFolderPath, "student_" + studentDto.getId() + ".png");
        assertTrue(Files.exists(filePath));

        verify(studentServiceMock, times(1)).findById(anyLong());
        verify(avatarRepositoryMock, times(1)).findByStudentId(anyLong());
    }

    @Test
    void uploadToIncorrectStudentIdTest() {
        assertThrows(IncorrectIdException.class, () -> out.upload(0L, avatarFileMock));
        assertThrows(IncorrectIdException.class, () -> out.upload(-1L, avatarFileMock));
    }

    @Test
    void uploadNullAvatarTest() {
        assertThrows(ParameterIsNullException.class, () -> out.upload(1L, null));
    }

    @Test
    void findAll() {
    }
}