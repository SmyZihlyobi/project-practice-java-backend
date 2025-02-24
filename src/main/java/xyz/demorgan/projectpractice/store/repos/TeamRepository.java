package xyz.demorgan.projectpractice.store.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.demorgan.projectpractice.store.entity.Team;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Team findByNameIgnoreCase(String name);
}