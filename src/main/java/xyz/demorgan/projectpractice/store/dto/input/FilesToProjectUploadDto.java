package xyz.demorgan.projectpractice.store.dto.input;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FilesToProjectUploadDto {
    @NotNull(message = "ID проекта не может быть пустым")
    @Min(value = 1, message = "ID проекта должен быть больше 0")
    Long projectId;
    @NotNull(message = "Файл не может быть пустым")
    MultipartFile file;
}
