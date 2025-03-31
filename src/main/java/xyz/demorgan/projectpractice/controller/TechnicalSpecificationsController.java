package xyz.demorgan.projectpractice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.demorgan.projectpractice.service.TechnicalSpecificationsService;
import xyz.demorgan.projectpractice.store.dto.input.FilesToProjectUploadDto;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Set;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@Tag(name = "TechnicalSpecifications controller", description = "Controller for uploading, downloading and deleting technicalSpecifications")
@Slf4j
@Validated
@RequestMapping("api/v1/files")
public class TechnicalSpecificationsController {
    TechnicalSpecificationsService technicalSpecificationsService;
    Validator validator;

    @Operation(summary = "Get technicalSpecifications", description = "Get technicalSpecifications by file name")
    @SecurityRequirement(name = "bearerAuth")
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
    @Operation(summary = "Upload technicalSpecifications", description = "Upload technicalSpecifications for project")
    @PostMapping(value = "/technicalSpecifications", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadTechnicalSpecifications(@ModelAttribute @Valid FilesToProjectUploadDto FilesToProjectUploadDto) {
        Set<ConstraintViolation<FilesToProjectUploadDto>> violations = validator.validate(FilesToProjectUploadDto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        log.info("Uploading technicalSpecifications for project {}", FilesToProjectUploadDto.getProjectId());
        return technicalSpecificationsService.uploadTechnicalSpecifications(FilesToProjectUploadDto.getProjectId(), FilesToProjectUploadDto.getFile());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete technicalSpecifications", description = "Delete technicalSpecifications by file name")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/technicalSpecifications/{fileName}")
    public ResponseEntity<?> deleteTechnicalSpecifications(@PathVariable String fileName) {
        log.info("Deleting technicalSpecifications {}", fileName);
        return technicalSpecificationsService.deleteTechnicalSpecifications(fileName);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete all technicalSpecifications", description = "Delete all technicalSpecifications")
    @SecurityRequirement(name = "bearerAuth")
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
