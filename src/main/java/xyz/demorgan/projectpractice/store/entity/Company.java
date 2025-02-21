package xyz.demorgan.projectpractice.store.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Company implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "representative")
    String representative;

    @Column(name = "contacts")
    String contacts;

    @Column(name = "is_student_company", columnDefinition = "boolean default false")
    boolean isStudentCompany;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Project> projects;
}
