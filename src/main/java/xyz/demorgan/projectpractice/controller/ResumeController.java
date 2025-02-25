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
import xyz.demorgan.projectpractice.service.ResumeService;
import xyz.demorgan.projectpractice.store.dto.input.ResumeUploadRequest;

import java.io.InputStream;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@Slf4j
@RequestMapping("api/v1/files")
public class ResumeController {
    ResumeService filesService;

    @GetMapping("/resume/{fileName}")
    public ResponseEntity<InputStreamResource> getResume(@PathVariable String fileName) {
        log.info("Getting resume {}", fileName);

        InputStream resume = filesService.getResume(fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                .body(new InputStreamResource(resume));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STUDENT')")
    @PostMapping("/resume")
    public ResponseEntity<?> uploadResume(@RequestBody ResumeUploadRequest resumeUploadRequest) {
        log.info("Uploading resume for user {}", resumeUploadRequest.getUserId());
        return filesService.uploadResume(resumeUploadRequest.getUserId(), resumeUploadRequest.getFile());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/resume/{fileName}")
    public ResponseEntity<?> deleteResume(@PathVariable String fileName) {
        log.info("Deleting resume {}", fileName);
        return filesService.deleteResume(fileName);
    }
}
