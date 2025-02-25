package xyz.demorgan.projectpractice.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import xyz.demorgan.projectpractice.service.TechnicalSpecificationsService;
import xyz.demorgan.projectpractice.store.dto.input.FilesToProjectUploadDto;

import java.io.InputStream;

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
    public ResponseEntity<?> uploadTechnicalSpecifications(@RequestBody FilesToProjectUploadDto FilesToProjectUploadDto) {
        log.info("Uploading technicalSpecifications for project {}", FilesToProjectUploadDto.getProjectId());
        return technicalSpecificationsService.uploadTechnicalSpecifications(FilesToProjectUploadDto.getProjectId(), FilesToProjectUploadDto.getFile());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/technicalSpecifications/{fileName}")
    public ResponseEntity<?> deleteTechnicalSpecifications(@PathVariable String fileName) {
        log.info("Deleting technicalSpecifications {}", fileName);
        return technicalSpecificationsService.deleteTechnicalSpecifications(fileName);
    }
}
