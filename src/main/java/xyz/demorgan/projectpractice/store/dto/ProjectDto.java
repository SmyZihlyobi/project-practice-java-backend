package xyz.demorgan.projectpractice.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for {@link xyz.demorgan.projectpractice.store.entity.Project}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDto {
    @NotNull
    private Long id;
    @NotNull
    @NotEmpty
    @NotBlank
    private String name;
    @NotNull
    @NotEmpty
    @NotBlank
    private String description;
    @NotNull
    @NotEmpty
    @NotBlank
    private String stack;
    private int teamsAmount;
    private String technicalSpecifications;
    private String presentation;
    private boolean isStudentProject;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}