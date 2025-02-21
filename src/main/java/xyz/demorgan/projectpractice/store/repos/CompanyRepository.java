package xyz.demorgan.projectpractice.store.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.demorgan.projectpractice.store.entity.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}