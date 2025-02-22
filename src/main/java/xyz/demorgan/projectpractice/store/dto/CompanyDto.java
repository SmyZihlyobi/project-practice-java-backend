package xyz.demorgan.projectpractice.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for {@link xyz.demorgan.projectpractice.store.entity.Company}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDto {
    private Long id;
    @NotNull
    @NotEmpty
    @NotBlank
    private String name;
    private String email;
    @NotNull
    @NotEmpty
    @NotBlank
    private String representative;
    @NotNull
    @NotEmpty
    @NotBlank
    private String contacts;
    private boolean isStudentCompany;
    @NotNull
    private LocalDateTime createdAt;
    private List<ProjectDto> projects;
}