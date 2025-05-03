package xyz.demorgan.projectpractice.store.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotBlank
    private String stack;
    private boolean active;
    private int teamsAmount;
    private String direction;
    private String requiredRoles;
    private String technicalSpecifications;
    private String presentation;
    private boolean studentProject;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    private LocalDateTime updatedAt;

    private String companyLink;
    private String companyName;
}