package xyz.demorgan.projectpractice.service;

import io.minio.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import xyz.demorgan.projectpractice.store.entity.Student;
import xyz.demorgan.projectpractice.store.repos.StudentRepository;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@Slf4j
public class ResumeService {
    MinioClient minioClient;
    StudentRepository studentRepository;
    FileValidateService fileValidateService;

    public ResponseEntity<?> uploadResume(Long userId, MultipartFile file) {
        fileValidateService.validateFile(file);

        try {
            minioClient.putObject(PutObjectArgs
                    .builder()
                    .bucket("resume")
                    .object(file.getOriginalFilename().replaceAll(" ", "").trim())
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

            Student student = studentRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student with this id not found"));

            student.setResumePdf(file.getOriginalFilename().replaceAll(" ", "_").trim()); // TODO сделать генерацию названия нормально и для ретерна

            studentRepository.save(student);

            HashMap<String, String> response = new HashMap<>();
            response.put("message", "File uploaded successfully");
            response.put("fileName", file.getOriginalFilename());

            return ResponseEntity.ok().body(response);

        } catch (Exception e) {
            log.error("Error uploading file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file");
        }
    }

    public InputStream getResume(String fileName) {
        try {
            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .bucket("resume")
                    .object(fileName)
                    .build();

            return minioClient.getObject(getObjectArgs);
        } catch (Exception e) {
            log.error("Error while getting resume", e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resume with this name not found");
        }
    }

    public ResponseEntity<?> deleteResume(String fileName) {
        try {
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                    .bucket("resume")
                    .object(fileName)
                    .build();

            minioClient.removeObject(removeObjectArgs);

            Student student = studentRepository.findByResumePdf(fileName);
            student.setResumePdf("");
            studentRepository.save(student);

            return ResponseEntity.ok().body("File deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting file");
        }
    }
}