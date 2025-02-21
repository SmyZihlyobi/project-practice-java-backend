package xyz.demorgan.projectpractice.service;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xyz.demorgan.projectpractice.exceptions.NotFound;
import xyz.demorgan.projectpractice.store.dto.CompanyDto;
import xyz.demorgan.projectpractice.store.mapper.CompanyMapper;
import xyz.demorgan.projectpractice.store.repos.CompanyRepository;

import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@AllArgsConstructor
@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CompanyService {
    CompanyMapper companyMapper;
    CompanyRepository companyRepository;

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
}
