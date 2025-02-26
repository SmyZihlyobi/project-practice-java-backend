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

import java.util.HashMap;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@Slf4j
public class PresentationService {
    MinioClient minioClient;
    ProjectRepository projectRepository;
    FileValidateService fileValidateService;
    public ResponseEntity<?> uploadPresentation(Long projectId, MultipartFile file) {
        fileValidateService.validateFile(file);

        try {
            minioClient.putObject(PutObjectArgs
                    .builder()
                    .bucket("presentation")
                    .object(file.getOriginalFilename().replaceAll(" ", "_").trim())
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project with this id not found"));

            project.setPresentation(file.getOriginalFilename().replaceAll(" ", "_").trim());

            projectRepository.save(project);

            HashMap<String, String> response = new HashMap<>();
            response.put("message", "Presentation uploaded successfully");
            response.put("fileName", file.getOriginalFilename().replaceAll(" ", "_").trim());

            return ResponseEntity.ok().body(response);

        } catch (Exception e) {
            log.error("Error uploading presentation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading presentation");
        }
    }

    public InputStream getPresentation(String fileName) {
        try {
            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .bucket("presentation")
                    .object(fileName)
                    .build();

            return minioClient.getObject(getObjectArgs);
        } catch (Exception e) {
            log.error("Error while getting presentation", e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resume with this name not found");
        }
    }

    public ResponseEntity<?> deletePresentation(String fileName) {
        try {
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                    .bucket("presentation")
                    .object(fileName)
                    .build();

            Project project = projectRepository.findByPresentation(fileName);
            project.setPresentation(null);
            projectRepository.save(project);


            minioClient.removeObject(removeObjectArgs);
            return ResponseEntity.ok().body("File deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting file");
        }
    }
}
