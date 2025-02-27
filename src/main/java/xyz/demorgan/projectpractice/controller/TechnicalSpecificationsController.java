package xyz.demorgan.projectpractice.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import xyz.demorgan.projectpractice.service.TechnicalSpecificationsService;
import xyz.demorgan.projectpractice.store.dto.input.FilesToProjectUploadDto;

import java.io.InputStream;
import java.util.HashMap;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@Slf4j
@RequestMapping("api/v1/files")
public class TechnicalSpecificationsController {
    TechnicalSpecificationsService technicalSpecificationsService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_COMPANY', 'ROLE_STUDENT')")
    @GetMapping("/technicalSpecifications/{fileName}")
    public ResponseEntity<InputStreamResource> getTechnicalSpecifications(@PathVariable String fileName) {
        log.info("Getting technicalSpecifications {}", fileName);

        InputStream resume = technicalSpecificationsService.getTechnicalSpecifications(fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                .body(new InputStreamResource(resume));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_COMPANY')")
    @PostMapping(value = "/technicalSpecifications", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadTechnicalSpecifications(@ModelAttribute @Valid FilesToProjectUploadDto FilesToProjectUploadDto) {
        log.info("Uploading technicalSpecifications for project {}", FilesToProjectUploadDto.getProjectId());
        return technicalSpecificationsService.uploadTechnicalSpecifications(FilesToProjectUploadDto.getProjectId(), FilesToProjectUploadDto.getFile());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/technicalSpecifications/{fileName}")
    public ResponseEntity<?> deleteTechnicalSpecifications(@PathVariable String fileName) {
        log.info("Deleting technicalSpecifications {}", fileName);
        return technicalSpecificationsService.deleteTechnicalSpecifications(fileName);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/technicalSpecifications/clear-bucket")
    public ResponseEntity<?> deleteAllObjectsInBucket() {
        try {
            technicalSpecificationsService.deleteAllObjectsInBucket();
            HashMap<String, String> response = new HashMap<>();
            response.put("message", "Все объекты из бакета technical-specifications успешно удалены.");

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            HashMap<String, String> response = new HashMap<>();
            response.put("message", "Ошибка при удалении объектов: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
