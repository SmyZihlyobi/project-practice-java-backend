package xyz.demorgan.projectpractice.store.dto.input;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


@Data
public class ResumeUploadRequest {
    @NotNull(message = "ID студента не может быть пустым")
    @Min(value = 1, message = "ID студента должен быть больше 0")
    Long userId;
    @NotNull(message = "Файл не может быть пустым")
    MultipartFile file;
}
