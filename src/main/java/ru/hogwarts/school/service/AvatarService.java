package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.dto.AvatarDto;
import ru.hogwarts.school.dto.StudentDto;
import ru.hogwarts.school.exception.AvatarNotFoundException;
import ru.hogwarts.school.exception.IncorrectIdException;
import ru.hogwarts.school.exception.ParameterIsNullException;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.repository.AvatarRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
public class AvatarService {

    Logger log = LoggerFactory.getLogger(AvatarService.class);

    @Value("${path.to.avatars.folder}")
    private String folder;

    private final AvatarRepository repository;
    private final StudentService studentService;

    public AvatarService(AvatarRepository repository, StudentService studentService) {
        this.repository = repository;
        this.studentService = studentService;
    }

    public AvatarDto find(long id) {
        log.info("Method find called with parameters: {}", id);

        idParameterChecker(id);
        AvatarDto result = AvatarDto.toDto(repository.findById(id)
                .orElseThrow(() -> new AvatarNotFoundException("Avatar with id " + id + " not found")));

        log.info("Method find completed with result: {}", result);
        return result;
    }

    public AvatarDto findByStudentId(long studentId) {
        log.info("Method findByStudentId called with parameters: {}", studentId);

        idParameterChecker(studentId);
        AvatarDto result = AvatarDto.toDto(repository.findByStudentId(studentId)
                .orElse(new Avatar()));

        log.info("Method findByStudentId completed with result: {}", result);
        return result;
    }

    public void upload(Long studentId, MultipartFile avatar) throws IOException {
        log.info("Method upload called with parameters: {}, {}", studentId, avatar);

        notNullParameterChecker(avatar);
        idParameterChecker(studentId);

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
        log.info("Method upload completed");
    }

    public Collection<AvatarDto> findAll(Integer pageNumber, Integer pageSize) {
        log.info("Method findAll called with parameters: {}, {}", pageNumber, pageSize);

        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        ArrayList<AvatarDto> result = repository
                .findAll(pageRequest)
                .getContent()
                .stream()
                .map(AvatarDto::toDto)
                .collect(Collectors.toCollection(ArrayList::new));

        log.info("Method findAll completed with result size: {}", result.size());
        return result;
    }

    private String getExtension(String fileName) {
        log.debug("Method getExtension called with parameters: {}", fileName);

        String result = fileName.substring(fileName.lastIndexOf(".") + 1);

        log.debug("Method getExtension completed with result: {}", result);
        return result;
    }

    private byte[] generateAvatarPreview(Path filePath) throws IOException {
        log.debug("Method generateAvatarPreview called with parameters: {}", filePath);

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
            byte[] result = baos.toByteArray();

            log.debug("Method generateAvatarPreview completed with result length: {}", result.length);
            return result;
        }
    }

    private void idParameterChecker(long id) {
        if (id < 1) {
            throw new IncorrectIdException("ID can't be less than 1");
        }
    }

    private void notNullParameterChecker(Object o) {
        if (o == null) {
            throw new ParameterIsNullException("Parameter can't be null");
        }
    }
}
