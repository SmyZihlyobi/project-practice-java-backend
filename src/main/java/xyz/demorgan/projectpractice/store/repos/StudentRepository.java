package xyz.demorgan.projectpractice.store.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.demorgan.projectpractice.store.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Student findByResumePdf(String resumePdf);
}