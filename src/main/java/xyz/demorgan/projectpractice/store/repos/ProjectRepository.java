package xyz.demorgan.projectpractice.store.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import xyz.demorgan.projectpractice.store.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Project findByTechnicalSpecifications(String technicalSpecifications);

    Project findByPresentation(String presentation);

    @Modifying
    @Query("UPDATE Project s SET s.technicalSpecifications = NULL")
    void updateTechnicalSpecificationsToNull();

    @Modifying
    @Query("UPDATE Project s SET s.presentation = NULL")
    void updatePresentationToNull();
}