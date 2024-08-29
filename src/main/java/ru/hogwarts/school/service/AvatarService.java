package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.dto.AvatarDto;
import ru.hogwarts.school.exception.AvatarNotFoundException;
import ru.hogwarts.school.repository.AvatarRepository;

@Service
public class AvatarService {

    @Value("${path.to.avatars.folder}")
    private String folder;

    private final AvatarRepository repository;
    private final StudentService studentService;

    public AvatarService(AvatarRepository repository, StudentService studentService) {
        this.repository = repository;
        this.studentService = studentService;
    }

    public AvatarDto find(long id) {
        return AvatarDto.toDto(repository.findById(id)
                .orElseThrow(() -> new AvatarNotFoundException("Аватар с id " + id + " не найден")));
    }

    public AvatarDto findByStudentId(long studentId) {
        return AvatarDto.toDto(repository.findByStudentId(studentId)
                .orElseThrow(() -> new AvatarNotFoundException("Аватар с student_id " + studentId + " не найден")));
    }
}
