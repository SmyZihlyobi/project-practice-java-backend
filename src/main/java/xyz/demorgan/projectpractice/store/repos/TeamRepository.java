package xyz.demorgan.projectpractice.store.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import xyz.demorgan.projectpractice.store.entity.Team;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Team findByNameIgnoreCase(String name);

    @Query("SELECT t FROM Team t " +
            "ORDER BY " +
            "CASE WHEN LOWER(t.name) = 'не выбрана' THEN 1 ELSE 0 END, " +
            "t.name ASC")
    List<Team> findAllByOrderByNameAsc();

}