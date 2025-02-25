package xyz.demorgan.projectpractice.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.demorgan.projectpractice.service.PresentationService;
import xyz.demorgan.projectpractice.store.dto.input.FilesToProjectUploadDto;

import java.io.InputStream;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@Slf4j
@RequestMapping("api/v1/files")
public class PresentationController {
    PresentationService presentationService;

    @GetMapping("/presentation/{fileName}")
    public ResponseEntity<InputStreamResource> getPresentation(@PathVariable String fileName) {
        log.info("Getting presentation {}", fileName);

        InputStream resume = presentationService.getPresentation(fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                .body(new InputStreamResource(resume));
    }

    @PostMapping("/presentation")
    public ResponseEntity<?> uploadPresentation(@RequestBody FilesToProjectUploadDto FilesToProjectUploadDto) {
        log.info("Uploading presentation for project {}", FilesToProjectUploadDto.getProjectId());
        return presentationService.uploadPresentation(FilesToProjectUploadDto.getProjectId(), FilesToProjectUploadDto.getFile());
    }

    @DeleteMapping("/presentation/{fileName}")
    public ResponseEntity<?> deletePresentation(@PathVariable String fileName) {
        log.info("Deleting presentation {}", fileName);
        return presentationService.deletePresentation(fileName);
    }
}
