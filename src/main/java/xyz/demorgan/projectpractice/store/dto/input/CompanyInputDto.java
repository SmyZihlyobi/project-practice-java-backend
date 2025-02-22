package xyz.demorgan.projectpractice.store.dto.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for {@link xyz.demorgan.projectpractice.store.entity.Company}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyInputDto {
    private String name;
    private String representative;
    private String contacts;
    private String email;
    private boolean isStudentCompany;
}