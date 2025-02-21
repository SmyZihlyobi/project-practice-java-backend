package xyz.demorgan.projectpractice.store.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Student implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(name = "team_name")
    String team_name;

    @Column(name = "group_id", nullable = false)
    String group_id;

    @Column(name = "year", nullable = false)
    int year;

    @Column(name = "last_name", nullable = false)
    String lastName;

    @Column(name = "first_name", nullable = false)
    String firstName;

    @Column(name = "patronymic")
    String patronymic;

    @Column(name = "first_priority", nullable = false)
    int firstPriority;

    @Column(name = "second_priority", nullable = false)
    int secondPriority;

    @Column(name = "third_priority", nullable = false)
    int thirdPriority;

    @Column(name = "other_priorities")
    String otherPriorities;

    @Column(name = "telegram", nullable = false)
    String telegram;

    @Column(name = "resume_pdf")
    String resumePdf;

    @Column(name = "resume_link")
    String resumeLink;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "team_id")
    Team team;
}
