package xyz.demorgan.projectpractice.store.dto.input;

import lombok.Data;

@Data
public class StudentInputDto {
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
    private String telegram;
    private String resumePdf;
    private String resumeLink;
}