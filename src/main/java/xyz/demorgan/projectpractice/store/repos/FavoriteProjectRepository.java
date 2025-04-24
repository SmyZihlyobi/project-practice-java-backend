package xyz.demorgan.projectpractice.store.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.demorgan.projectpractice.store.entity.FavoriteProject;

import java.util.List;
import java.util.Optional;

public interface FavoriteProjectRepository extends JpaRepository<FavoriteProject, Long> {
    Optional<List<FavoriteProject>> findAllByStudentId(Long studentId);
}