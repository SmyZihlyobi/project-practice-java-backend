package xyz.demorgan.projectpractice.store.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank
    private String name;
    @NotBlank
    private String representative;
    @NotBlank
    private String contacts;
    @JsonProperty("isStudentCompany")
    private boolean studentCompany;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    private LocalDateTime createdAt;
    private String website;
    private List<ProjectDto> projects;
}