package xyz.demorgan.projectpractice.store.dto.input;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StudentInputDto {
    @NotBlank(message = "Название команды не может быть пустым")
    private String teamName;
    @NotBlank(message = "ID группы не может быть пустым")
    private String groupId;
    @NotBlank(message = "Курс студента не может быть пустым")
    @Min(value = 1, message = "Курс студента должен быть больше 0")
    @Max(value = 3, message = "Курс студента должен быть меньше 3")
    private int year;
    @NotBlank(message = "Фамилия студента не может быть пустой")
    private String lastName;
    @NotBlank(message = "Имя студента не может быть пустым")
    private String firstName;
    private String patronymic;
    @NotBlank(message = "Первый приоритет не может быть пустым")
    private int firstPriority;
    @NotBlank(message = "Второй приоритет не может быть пустым")
    private int secondPriority;
    @NotBlank(message = "Третий приоритет не может быть пустым")
    private int thirdPriority;
    private String otherPriorities;
    @NotBlank(message = "Telegram не может быть пустым")
    private String telegram;
    private String resumePdf;
    private String resumeLink;
}