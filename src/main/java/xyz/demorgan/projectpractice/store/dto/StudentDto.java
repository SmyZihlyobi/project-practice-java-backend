package xyz.demorgan.projectpractice.store.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for {@link xyz.demorgan.projectpractice.store.entity.Student}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentDto {
    private Long id;
    private String team_name;
    private String group_id;
    private int year;
    private String lastName;
    private String firstName;
    private String patronymic;
    private int firstPriority;
    private int secondPriority;
    private int thirdPriority;
    private String otherPriorities;
    private String telegram;
    private String resumePdf;
    private String resumeLink;
    private LocalDateTime createdAt;
}