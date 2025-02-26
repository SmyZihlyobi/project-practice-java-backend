package xyz.demorgan.projectpractice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import xyz.demorgan.projectpractice.store.entity.Company;
import xyz.demorgan.projectpractice.store.repos.CompanyRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminCompanyInitializer implements CommandLineRunner {

    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${company.default.email}")
    private String defaultEmail;

    @Value("${company.default.password}")
    private String defaultPassword;

    @Override
    public void run(String... args) throws Exception {

        Optional<Company> existingCompanyByEmail = companyRepository.findByEmail(defaultEmail);
        Optional<Company> existingCompanyById = companyRepository.findById(1L);

        if (existingCompanyByEmail.isPresent() || existingCompanyById.isPresent()) {
            log.warn("Компания с email {} или ID {} уже существует. Пропускаем создание.", defaultEmail, 1L);
            return;
        }

        Company company = new Company();
        company.setId(1L);
        company.setName("ИКНТ");
        company.setRepresentative("ИКНТ");
        company.setContacts("ИКНТ");
        company.setEmail(defaultEmail);
        company.setPassword(passwordEncoder.encode(defaultPassword));
        company.setStudentCompany(false);
        company.setCreatedAt(LocalDateTime.now());

        List<String> roles = new ArrayList<>();
        roles.add("ROLE_ADMIN");
        roles.add("ROLE_COMPANY");
        company.setRoles(roles);

        companyRepository.save(company);

        log.warn("================================================");
        log.warn("Создана компания по умолчанию.");
        log.warn("ID: {}", company.getId());
        log.warn("Название: {}", company.getName());
        log.warn("Email: {}", company.getEmail());
        log.warn("Email: {}", defaultPassword);
        log.warn("НЕОБХОДИМО ИЗМЕНИТЬ ПАРОЛЬ ПОСЛЕ ПЕРВОГО ВХОДА!");
        log.warn("================================================");
    }
}