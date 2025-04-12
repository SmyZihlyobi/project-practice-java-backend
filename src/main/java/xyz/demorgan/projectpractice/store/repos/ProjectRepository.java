package xyz.demorgan.projectpractice.store.repos;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import xyz.demorgan.projectpractice.store.entity.Project;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Project findByTechnicalSpecifications(String technicalSpecifications);

    Project findByPresentation(String presentation);

    @Modifying
    @Transactional
    @Query("UPDATE Project s SET s.technicalSpecifications = NULL")
    void updateTechnicalSpecificationsToNull();

    @Modifying
    @Transactional
    @Query("UPDATE Project s SET s.presentation = NULL")
    void updatePresentationToNull();

    @Query("SELECT p FROM Project p ORDER BY p.active DESC, p.createdAt DESC")
    List<Project> findAllSorted();
}