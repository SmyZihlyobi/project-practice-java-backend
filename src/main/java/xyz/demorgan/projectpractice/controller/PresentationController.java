package xyz.demorgan.projectpractice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import xyz.demorgan.projectpractice.service.PresentationService;
import xyz.demorgan.projectpractice.store.dto.input.FilesToProjectUploadDto;

import java.io.InputStream;
import java.util.HashMap;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@Tag(name = "Presentation controller", description = "Controller for uploading, downloading and deleting presentations")
@Slf4j
@RequestMapping("api/v1/files")
public class PresentationController {
    PresentationService presentationService;

    @Operation(summary = "Get presentation", description = "Get presentation by file name")
    @GetMapping("/presentation/{fileName}")
    public ResponseEntity<InputStreamResource> getPresentation(@PathVariable String fileName) {
        log.info("Getting presentation {}", fileName);

        InputStream resume = presentationService.getPresentation(fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                .body(new InputStreamResource(resume));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_COMPANY')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Upload presentation", description = "Upload presentation for project")
    @PostMapping(value = "/presentation", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadPresentation(@ModelAttribute @Valid FilesToProjectUploadDto FilesToProjectUploadDto) {
        log.info("Uploading presentation for project {}", FilesToProjectUploadDto.getProjectId());
        return presentationService.uploadPresentation(FilesToProjectUploadDto.getProjectId(), FilesToProjectUploadDto.getFile());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete presentation", description = "Delete presentation by file name")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/presentation/{fileName}")
    public ResponseEntity<?> deletePresentation(@PathVariable String fileName) {
        log.info("Deleting presentation {}", fileName);
        return presentationService.deletePresentation(fileName);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete all presentations", description = "Delete all presentations")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/presentation/clear-bucket")
    public ResponseEntity<?> deleteAllObjectsInBucket() {
        try {
            presentationService.deleteAllObjectsInBucket();
            HashMap<String, String> response = new HashMap<>();
            response.put("message", "Все объекты из бакета presentation успешно удалены.");

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            HashMap<String, String> response = new HashMap<>();
            response.put("message", "Ошибка при удалении объектов: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
