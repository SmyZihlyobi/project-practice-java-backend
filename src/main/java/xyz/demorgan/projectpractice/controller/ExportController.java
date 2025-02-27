package xyz.demorgan.projectpractice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.demorgan.projectpractice.service.ExcelService;
import xyz.demorgan.projectpractice.store.dto.StudentExportDto;
import xyz.demorgan.projectpractice.store.mapper.StudentMapper;
import xyz.demorgan.projectpractice.store.repos.StudentRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Tag(name = "Export controller", description = "Controller for exporting students to Excel or JSON")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
public class ExportController {

    ExcelService excelService;
    StudentRepository studentRepository;
    StudentMapper studentMapper;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Export students to Excel file", description = "Export all students to Excel file")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/export/students/file")
    public ResponseEntity<ByteArrayResource> exportToExcel() throws IOException {
        Workbook workbook = excelService.exportStudents();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=students.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(resource.contentLength())
                .body(resource);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Export students to JSON", description = "Export all students to JSON, можно использовать в будущем для алгоритма распределения")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/export/students")
    public ResponseEntity<List<StudentExportDto>> getStudents() {
        return ResponseEntity.ok(
                studentRepository.findAllByOrderByTeamNameAsc()
                        .stream()
                        .map(studentMapper::toStudentExportDto)
                        .collect(Collectors.toList()));
    }
}
