package xyz.demorgan.projectpractice.store.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for {@link xyz.demorgan.projectpractice.store.entity.Student}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentExportDto {
    private Long id;
    private String teamName;
    private String groupId;
    private int year;
    private String lastName;
    private String firstName;
    private String patronymic;
    private int firstPriority;
    private int secondPriority;
    private int thirdPriority;
    private String otherPriorities;
    private String desiredRole;
    private String telegram;
    private String resumePdf;
    private String resumeLink;
}