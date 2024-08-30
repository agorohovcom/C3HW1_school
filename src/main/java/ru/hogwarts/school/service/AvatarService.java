package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.dto.AvatarDto;
import ru.hogwarts.school.dto.StudentDto;
import ru.hogwarts.school.exception.AvatarNotFoundException;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.repository.AvatarRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

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
                .orElse(new Avatar()));
    }

    public void upload(Long studentId, MultipartFile avatar) throws IOException {
        StudentDto studentDto = studentService.findById(studentId);
        Path filePath = Path.of(folder, "student_" + studentDto.getId() + "." + getExtension(Objects.requireNonNull(avatar.getOriginalFilename())));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        try (InputStream is = avatar.getInputStream();
             OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             BufferedOutputStream bos = new BufferedOutputStream(os, 1024)) {
            bis.transferTo(bos);
        }

        AvatarDto avatarDto = findByStudentId(studentId);
        avatarDto.setFilePath(filePath.toString());
        avatarDto.setFileSize(avatar.getSize());
        avatarDto.setMediaType(avatar.getContentType());
        avatarDto.setData(generateAvatarPreview(filePath));
        avatarDto.setStudentDto(studentDto);

        repository.save(AvatarDto.toEntity(avatarDto));
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private byte[] generateAvatarPreview(Path filePath) throws IOException {
        try (InputStream is = Files.newInputStream(filePath);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            BufferedImage image = ImageIO.read(bis);

            int height = image.getHeight() / (image.getWidth() / 100);
            BufferedImage preview = new BufferedImage(100, height, image.getType());
            Graphics2D graphics2D = preview.createGraphics();
            graphics2D.drawImage(image, 0, 0, 100, height, null);
            graphics2D.dispose();

            ImageIO.write(preview, getExtension(filePath.getFileName().toString()), baos);
            return baos.toByteArray();
        }
    }
}
