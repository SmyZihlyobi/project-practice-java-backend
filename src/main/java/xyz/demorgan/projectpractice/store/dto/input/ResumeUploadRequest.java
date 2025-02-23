package xyz.demorgan.projectpractice.store.dto.input;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


@Data
public class ResumeUploadRequest {
    Long userId;
    MultipartFile file;
}
