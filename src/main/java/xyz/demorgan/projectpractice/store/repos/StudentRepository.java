package xyz.demorgan.projectpractice.store.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import xyz.demorgan.projectpractice.store.entity.Student;
import xyz.demorgan.projectpractice.store.entity.Team;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Student findByResumePdf(String resumePdf);

    List<Student> findAllByTeam(Team team);

    List<Student> findAllByOrderByTeamNameAsc();

    @Modifying
    @Query("UPDATE Student s SET s.resumePdf = NULL")
    void updateResumePdfToNull();
}