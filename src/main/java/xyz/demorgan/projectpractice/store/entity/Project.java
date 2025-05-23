package xyz.demorgan.projectpractice.store.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.GenericGenerator;

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
    @GeneratedValue(generator = "project_gen")
    @GenericGenerator(name = "project_gen", strategy = "xyz.demorgan.projectpractice.store.repos.ProjectIdGenerator")
    @Column(name = "id", nullable = false)
    Long id;

    @Column(name = "is_active", columnDefinition = "boolean default true", nullable = false)
    boolean active;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    String description;

    @Column(name = "stack", nullable = false)
    String stack;

    @Column(name = "teams_amount", columnDefinition = "int default 1", nullable = false)
    int teamsAmount;

    @Column(name = "technical_specifications")
    String technicalSpecifications;

    @Column(name = "presentation")
    String presentation;

    @Column(name = "is_student_project", columnDefinition = "boolean default false", nullable = false)
    boolean studentProject;

    @Column(name = "direction")
    String direction;

    @Column(name = "required_roles")
    String requiredRoles;

    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "company_id")
    Company company;

}

