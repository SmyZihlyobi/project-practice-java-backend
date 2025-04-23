package xyz.demorgan.projectpractice.store.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private String username;
    private String teamName;
    private String groupId;
    private int year;
    private String lastName;
    private String firstName;
    private String patronymic;
    private int firstPriority;
    private int secondPriority;
    private int thirdPriority;
    private String desiredRole;
    private String otherPriorities;
    private String telegram;
    private String resumePdf;
    private String resumeLink;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    private LocalDateTime createdAt;
}