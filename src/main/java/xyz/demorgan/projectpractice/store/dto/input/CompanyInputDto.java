package xyz.demorgan.projectpractice.store.dto.input;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "Название компании не может быть пустым")
    private String name;
    @NotBlank(message = "Представитель компании не может быть пустым")
    private String representative;
    @NotBlank(message = "Контакты не могут быть пустыми")
    private String contacts;
    @Email(message = "Некорректный email")
    @NotBlank(message = "Email не может быть пустым")
    private String email;
    private boolean studentCompany;
}