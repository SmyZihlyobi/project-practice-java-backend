package xyz.demorgan.projectpractice.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/presentation/{fileName}")
    public ResponseEntity<InputStreamResource> getResume(@PathVariable String fileName) {
        log.info("Getting presentation {}", fileName);

        InputStream resume = technicalSpecificationsService.getTechnicalSpecifications(fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                .body(new InputStreamResource(resume));
    }

    @PostMapping("/presentation")
    public ResponseEntity<?> uploadResume(@RequestBody FilesToProjectUploadDto FilesToProjectUploadDto) {
        log.info("Uploading presentation for project {}", FilesToProjectUploadDto.getProjectId());
        return technicalSpecificationsService.uploadTechnicalSpecifications(FilesToProjectUploadDto.getProjectId(), FilesToProjectUploadDto.getFile());
    }

    @DeleteMapping("/presentation/{fileName}")
    public ResponseEntity<?> deleteResume(@PathVariable String fileName) {
        log.info("Deleting presentation {}", fileName);
        return technicalSpecificationsService.deleteTechnicalSpecifications(fileName);
    }
}
