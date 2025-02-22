package xyz.demorgan.projectpractice.service;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import xyz.demorgan.projectpractice.exceptions.NotFound;
import xyz.demorgan.projectpractice.store.dto.CompanyDto;
import xyz.demorgan.projectpractice.store.dto.input.CompanyInputDto;
import xyz.demorgan.projectpractice.store.entity.Company;
import xyz.demorgan.projectpractice.store.mapper.CompanyMapper;
import xyz.demorgan.projectpractice.store.repos.CompanyRepository;

import java.time.LocalDateTime;
import java.util.List;
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

    @Cacheable(value = "companies", key = "'all_companies'")
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

    public void approveCompany(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        String generatedPassword = generateRandomPassword();
        company.setPassword(passwordEncoder.encode(generatedPassword));
        companyRepository.save(company);

        System.out.println("Your account has been approved Your password is: " + generatedPassword);
//        emailService.sendEmail(company.getEmail(), "Your account has been approved Your password is: " + generatedPassword);
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
