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
import xyz.demorgan.projectpractice.service.ResumeService;
import xyz.demorgan.projectpractice.store.dto.input.ProjectInputDto;
import xyz.demorgan.projectpractice.store.dto.input.ResumeUploadRequest;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Set;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@Slf4j
@Validated
@Tag(name = "Resume controller", description = "Controller for uploading, downloading and deleting resumes")
@RequestMapping("api/v1/files")
public class ResumeController {
    ResumeService resumeService;
    Validator validator;

    @Operation(summary = "Get resume", description = "Get resume by file name")
    @GetMapping("/resume/{fileName}")
    public ResponseEntity<InputStreamResource> getResume(@PathVariable String fileName) {
        log.info("Getting resume {}", fileName);

        InputStream resume = resumeService.getResume(fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                .body(new InputStreamResource(resume));
    }

    //@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STUDENT')") TODO
    @Operation(summary = "Upload resume", description = "Upload resume for user")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(value = "/resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadResume(@ModelAttribute @Valid ResumeUploadRequest resumeUploadRequest) {
        Set<ConstraintViolation<ResumeUploadRequest>> violations = validator.validate(resumeUploadRequest);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        log.info("Uploading resume for user {}", resumeUploadRequest.getUserId());
        return resumeService.uploadResume(resumeUploadRequest.getUserId(), resumeUploadRequest.getFile());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete resume", description = "Delete resume by file name")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/resume/{fileName}")
    public ResponseEntity<?> deleteResume(@PathVariable String fileName) {
        log.info("Deleting resume {}", fileName);
        return resumeService.deleteResume(fileName);
    }

    @Operation(summary = "Delete all resumes", description = "Delete all resumes")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/resume/clear-bucket")
    public ResponseEntity<?> deleteAllObjectsInBucket() {
        try {
            resumeService.deleteAllObjectsInBucket();
            HashMap<String, String> response = new HashMap<>();
            response.put("message", "Все объекты из бакета resume успешно удалены.");

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            HashMap<String, String> response = new HashMap<>();
            response.put("message", "Ошибка при удалении объектов: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
