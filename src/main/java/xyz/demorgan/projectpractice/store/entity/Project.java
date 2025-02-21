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
public class Project implements Serializable {
    @Id
    @Column(name = "id", nullable = false)
    Long id;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "description", columnDefinition = "TEXT")
    String description;

    @Column(name = "stack")
    String stack;

    @Column(name = "teams_amount", columnDefinition = "int default 1")
    int teamsAmount;

    @Column(name = "technical_specifications")
    String technicalSpecifications;

    @Column(name = "presentation")
    String presentation;

    @Column(name = "is_student_project", columnDefinition = "boolean default false")
    boolean isStudentProject;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "company_id")
    Company company;
}

