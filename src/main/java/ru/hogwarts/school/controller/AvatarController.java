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
import java.util.Collection;

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

    @GetMapping()
    public ResponseEntity<Collection<AvatarDto>> findAll(
            @RequestParam("page") Integer pageNumber,
            @RequestParam("size") Integer pageSize) throws IOException {
        var avatarDtoList = service
                .findAll(pageNumber, pageSize)
                .stream()
                .toList();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(avatarDtoList);
    }

    // возвращает ссылки на скачивание аватарок xD
//    @GetMapping(value = "previews")
//    public void findAllAvatarPreviews(
//            @RequestParam("page") Integer pageNumber,
//            @RequestParam("size") Integer pageSize,
//            HttpServletResponse response) throws IOException {
//        var avatarDtoList = service
//                .findAllAvatarPreviews(pageNumber, pageSize)
//                .stream()
//                .toList();
//
//        response.setStatus(200);
//        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
//        response.setContentLength(avatarDtoList.stream().mapToInt(e -> e.getData().length).sum());
//
//        try(OutputStream os = response.getOutputStream();
//        BufferedOutputStream bos = new BufferedOutputStream(os, 1024)) {
//            for (AvatarDto a : avatarDtoList) {
//                bos.write(a.getData());
//            }
//        }
//    }
}
