package xyz.demorgan.projectpractice.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
@Service
public class FileValidateService {
    public void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        String contentType = file.getContentType().toLowerCase();
        if (!contentType.startsWith("application/pdf") &&
                !contentType.startsWith("application/vnd.ms-powerpoint") &&
                !contentType.startsWith("application/vnd.openxmlformats-officedocument.presentationml.presentation") &&
                !contentType.startsWith("application/msword") &&
                !contentType.startsWith("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
            throw new IllegalArgumentException("File is not a valid document or presentation");
        }
        String fileName = file.getOriginalFilename();
        if (fileName != null && !fileName.toLowerCase().matches(".*\\.(pdf|ppt|pptx|doc|docx)$")) {
            throw new IllegalArgumentException("Недопустимое расширение файла");
        }
    }
}
