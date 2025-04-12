package xyz.demorgan.projectpractice.store.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Student implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(name = "username", nullable = false, unique = true)
    String username;

    @Column(name = "team_name")
    String teamName;

    @Column(name = "group_id")
    String groupId;

    @Column(name = "year")
    Integer year;

    @Column(name = "last_name")
    String lastName;

    @Column(name = "first_name")
    String firstName;

    @Column(name = "patronymic")
    String patronymic;

    @Column(name = "first_priority")
    Integer firstPriority;

    @Column(name = "second_priority")
    Integer secondPriority;

    @Column(name = "third_priority")
    Integer thirdPriority;

    @Column(name = "other_priorities")
    String otherPriorities;

    @Column(name = "telegram")
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

    @ElementCollection
    @Column(name = "role", nullable = false)
    List<String> roles = new ArrayList<>(Collections.singletonList("ROLE_STUDENT"));
}
