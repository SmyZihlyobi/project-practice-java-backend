package xyz.demorgan.projectpractice.service;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import xyz.demorgan.projectpractice.config.jwt.JwtTokenUtils;
import xyz.demorgan.projectpractice.exceptions.NotFound;
import xyz.demorgan.projectpractice.store.dto.CompanyDto;
import xyz.demorgan.projectpractice.store.dto.LoginAnswer;
import xyz.demorgan.projectpractice.store.dto.input.CompanyInputDto;
import xyz.demorgan.projectpractice.store.dto.input.CompanyLoginDto;
import xyz.demorgan.projectpractice.store.entity.Company;
import xyz.demorgan.projectpractice.store.mapper.CompanyMapper;
import xyz.demorgan.projectpractice.store.repos.CompanyRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@AllArgsConstructor
@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CompanyService {
    CompanyMapper companyMapper;
    PasswordEncoder passwordEncoder;
    CompanyRepository companyRepository;
    AuthenticationManager authenticationManager;
    JwtTokenUtils jwtTokenUtils;
    EmailService emailService;

    public List<CompanyDto> getAll() {
        log.info("Getting all companies at {}", System.currentTimeMillis());
        return companyRepository.findAll()
                .stream()
                .map(companyMapper::toCompanyDto)
                .collect(Collectors.toList());
    }

    public CompanyDto getById(Long id) {
        log.info("Getting company with id: {} at {}", id, System.currentTimeMillis());
        return companyMapper.toCompanyDto(companyRepository.findById(id)
                .orElseThrow(() -> new NotFound("Company with id " + id + " not found")));
    }

    public Company create(CompanyInputDto companyInputDto) {
        log.info("Creating company at {}", System.currentTimeMillis());
        Company company = companyMapper.toEntity(companyInputDto);
        company.setCreatedAt(LocalDateTime.now());
        return companyRepository.save(company);
    }

    public CompanyDto delete(Long id) {
        log.info("Deleting company with id: {} at {}", id, System.currentTimeMillis());
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new NotFound("Company with id " + id + " not found"));
        companyRepository.delete(company);
        return companyMapper.toCompanyDto(company);
    }

    public Map<String, String> deleteAllCompanies() {
        log.info("Deleting all companies at {}", System.currentTimeMillis());
        companyRepository.deleteAll();
        return Map.of("message", "All companies deleted");
    }

    public void approveCompany(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new NotFound("Company not found"));

        String generatedPassword = generateRandomPassword();
        company.setPassword(passwordEncoder.encode(generatedPassword));
        companyRepository.save(company);

        emailService.sendEmail(generatedPassword); // ЯДЕРНЫЙ ТЕСТОВЫЙ КОСТЫЛЬ СНЕСТИ ОТ ГРЕХА ПОДАЛЬШЕ
    }

    public ResponseEntity<?> login(CompanyLoginDto companyLoginDto) {
        log.info("User login by email: {}", companyLoginDto.getEmail());

        try {
            log.debug("Attempting authentication...");
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            companyLoginDto.getEmail(),
                            companyLoginDto.getPassword()
                    )
            );
            log.info("Authentication result: {}", auth.isAuthenticated());

        } catch (BadCredentialsException e) {
            log.error("Authentication failed: {}", e.getMessage());
            return ResponseEntity.status(401).body("Invalid credentials");
        } catch (Exception e) {
            log.error("Unexpected error: ", e);
            return ResponseEntity.internalServerError().build();
        }

        log.info("User {} authenticated", companyLoginDto.getEmail());
        Company user = companyRepository.findByEmail(companyLoginDto.getEmail())
                .orElseThrow(() -> new NotFound("User not found with email: " + companyLoginDto.getEmail()));

        String token = jwtTokenUtils.generateCompanyToken(user);

        return ResponseEntity.ok(new LoginAnswer(token));
    }

    public void changePassword(String email) {
        Company company = companyRepository.findByEmail(email)
                .orElseThrow(() -> new NotFound("Company with email " + email + " not found"));

        String generatedPassword = generateRandomPassword();
        company.setPassword(passwordEncoder.encode(generatedPassword));
        companyRepository.save(company);

        emailService.sendEmail(generatedPassword); // TODO ЯДЕРНЫЙ ТЕСТОВЫЙ КОСТЫЛЬ СНЕСТИ ОТ ГРЕХА ПОДАЛЬШЕ
    }

    public List<CompanyDto> findUnapprovedCompanies() {
        log.info("Getting unapproved companies at {}", System.currentTimeMillis());
        return companyRepository.findUnapprovedCompanies()
                .orElseThrow(() -> new NotFound("No unapproved companies found"))
                .stream()
                .map(companyMapper::toCompanyDto)
                .collect(Collectors.toList());
    }

    private String generateRandomPassword() {
        int length = 15;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }
}
