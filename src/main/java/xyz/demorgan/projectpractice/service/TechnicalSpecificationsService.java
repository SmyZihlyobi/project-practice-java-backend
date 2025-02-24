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
import xyz.demorgan.projectpractice.store.entity.Project;
import xyz.demorgan.projectpractice.store.repos.ProjectRepository;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Optional;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@Slf4j
public class TechnicalSpecificationsService {
    MinioClient minioClient;
    ProjectRepository projectRepository;
    FileValidateService fileValidateService;

    public ResponseEntity<?> uploadTechnicalSpecifications(Long projectId, MultipartFile file) {
        fileValidateService.validateFile(file);
        try {
            minioClient.putObject(PutObjectArgs
                    .builder()
                    .bucket("technical-specifications")
                    .object(file.getOriginalFilename().replaceAll(" ", "").trim())
                    .stream(Files.newInputStream(Paths.get(file.getOriginalFilename())), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project with this id not found"));

            project.setTechnicalSpecifications(file.getOriginalFilename().replaceAll(" ", "").trim());

            projectRepository.save(project);

            HashMap<String, String> response = new HashMap<>();
            response.put("message", "File uploaded successfully");
            response.put("fileName", file.getOriginalFilename());

            return ResponseEntity.ok().body(response);

        } catch (Exception e) {
            log.error("Error uploading file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file");
        }
    }

    public InputStream getTechnicalSpecifications(String fileName) {
        try {
            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .bucket("technical-specifications")
                    .object(fileName)
                    .build();

            return minioClient.getObject(getObjectArgs);
        } catch (Exception e) {
            log.error("Error while getting technical specifications", e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Technical specification with this name not found");
        }
    }

    public ResponseEntity<?> deleteTechnicalSpecifications(String fileName) {
        try {
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                    .bucket("technical-specifications")
                    .object(fileName)
                    .build();

            minioClient.removeObject(removeObjectArgs);
            Project project = projectRepository.findByTechnicalSpecifications(fileName);

            project.setTechnicalSpecifications("");

            projectRepository.save(project);

            HashMap<String, String> response = new HashMap<>();
            response.put("message", "File deleted successfully");

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("Error deleting file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting file");
        }
    }
}
