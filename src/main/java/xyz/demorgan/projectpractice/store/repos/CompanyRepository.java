package xyz.demorgan.projectpractice.store.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.demorgan.projectpractice.store.entity.Company;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByEmail(String email);
}