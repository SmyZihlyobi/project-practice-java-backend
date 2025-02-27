package xyz.demorgan.projectpractice.store.dto.input;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectInputDto {
    @NotBlank(message = "Название проекта не может быть пустым")
    String name;
    @NotBlank(message = "Описание проекта не может быть пустым")
    String description;
    @NotBlank(message = "Количество команд на проекте не может быть пустой")
    @Min(value = 1, message = "Количество команд на проекте должно быть больше 0")
    int teamsAmount;
    @NotBlank(message = "Стек технологий не может быть пустым")
    String stack;
    String technicalSpecifications;
    Long presentation;
    boolean studentProject;
    int companyId;
}
