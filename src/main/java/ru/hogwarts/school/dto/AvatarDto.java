package ru.hogwarts.school.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.hogwarts.school.model.Avatar;

import java.util.Arrays;
import java.util.Objects;

public class AvatarDto {
    private Long id;
    private String filePath;
    private long fileSize;
    private String mediaType;
    @JsonIgnore
    private byte[] data;
    private StudentDto studentDto;

    public static AvatarDto toDto(Avatar avatar) {
        AvatarDto avatarDto = new AvatarDto();
        avatarDto.setId(avatar.getId());
        avatarDto.setFilePath(avatar.getFilePath());
        avatarDto.setFileSize(avatar.getFileSize());
        avatarDto.setMediaType(avatar.getMediaType());
        avatarDto.setData(avatar.getData());
        if (avatar.getStudent() != null) {
            avatarDto.setStudentDto(StudentDto.toDto(avatar.getStudent()));
        }
        return avatarDto;
    }

    public static Avatar toEntity(AvatarDto dto) {
        Avatar avatar = new Avatar();
        if (dto.id != null) {
            avatar.setId(dto.getId());
        }
        avatar.setFilePath(dto.getFilePath());
        avatar.setFileSize(dto.getFileSize());
        avatar.setMediaType(dto.getMediaType());
        avatar.setData(dto.getData());
        avatar.setStudent(StudentDto.toEntity(dto.getStudentDto()));
        return avatar;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public StudentDto getStudentDto() {
        return studentDto;
    }

    public void setStudentDto(StudentDto studentDto) {
        this.studentDto = studentDto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AvatarDto avatarDto = (AvatarDto) o;
        return fileSize == avatarDto.fileSize && Objects.equals(id, avatarDto.id) && Objects.equals(filePath, avatarDto.filePath) && Objects.equals(mediaType, avatarDto.mediaType) && Objects.deepEquals(data, avatarDto.data) && Objects.equals(studentDto, avatarDto.studentDto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, filePath, fileSize, mediaType, Arrays.hashCode(data), studentDto);
    }

    @Override
    public String toString() {
        return "AvatarDto{" +
                "id=" + id +
                ", filePath='" + filePath + '\'' +
                ", fileSize=" + fileSize +
                ", mediaType='" + mediaType + '\'' +
//                ", data=" + Arrays.toString(data) +
                ", data length=" + data.length +
                ", studentDto=" + studentDto +
                '}';
    }
}
