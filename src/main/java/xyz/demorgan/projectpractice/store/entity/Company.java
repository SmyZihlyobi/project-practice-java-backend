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
public class Company implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(name = "name", nullable = false, unique = true)
    String name;

    @Column(name = "representative", nullable = false)
    String representative;

    @Column(name = "contacts", nullable = false)
    String contacts;

    @Column(name = "email", nullable = false, unique = true)
    String email;

    @Column(name = "password")
    String password;

    @Column(name = "website")
    String website;

    @Column(name = "student_company", columnDefinition = "boolean default false", nullable = false)
    boolean studentCompany;

    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

    @ElementCollection
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role", nullable = false)
    List<String> roles = new ArrayList<>(Collections.singletonList("ROLE_COMPANY"));

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Project> projects = new ArrayList<>();
    ;
}
