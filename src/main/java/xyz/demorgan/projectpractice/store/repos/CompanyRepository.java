package xyz.demorgan.projectpractice.store.repos;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import xyz.demorgan.projectpractice.store.entity.Company;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByEmail(String email);

    @Transactional
    @Query("SELECT c FROM Company c WHERE c.password IS NULL")
    Optional<List<Company>> findUnapprovedCompanies();

    @Transactional
    @Query("SELECT c FROM Company c WHERE c.password IS NOT NULL")
    Optional<List<Company>> findAllApprovedCompanies();

    @Modifying
    @Query("DELETE FROM Company c WHERE c.id != :id OR c.email != :email")
    void deleteAllExceptAdmin(@Param("id") Long id, @Param("email") String email);
}