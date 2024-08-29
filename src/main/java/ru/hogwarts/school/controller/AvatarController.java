package ru.hogwarts.school.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.dto.AvatarDto;
import ru.hogwarts.school.service.AvatarService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("avatar")
public class AvatarController {
    private final AvatarService service;

    public AvatarController(AvatarService service) {
        this.service = service;
    }

    @PostMapping(value = "{studentId}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> upload(@PathVariable Long studentId, @RequestParam MultipartFile avatar) throws IOException {
        service.upload(studentId, avatar);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "{id}/get-avatar-from-db")
    public ResponseEntity<byte[]> download(@PathVariable Long id) {
        AvatarDto avatarDto = service.find(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(avatarDto.getMediaType()));
        headers.setContentLength(avatarDto.getData().length);
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(avatarDto.getData());
    }

    @GetMapping(value = "{id}/get-avatar-from-file")
    public void download(@PathVariable Long id, HttpServletResponse response) throws IOException {
        AvatarDto avatarDto = service.find(id);
        Path path = Path.of(avatarDto.getFilePath());
        try (InputStream is = Files.newInputStream(path);
             OutputStream os = response.getOutputStream();
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             BufferedOutputStream bos = new BufferedOutputStream(os, 1024)) {
            response.setStatus(200);
            response.setContentType(avatarDto.getMediaType());
            response.setContentLength((int) avatarDto.getFileSize());
            bis.transferTo(bos);
        }
    }
}
