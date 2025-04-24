package xyz.demorgan.projectpractice.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import xyz.demorgan.projectpractice.service.CompanyService;
import xyz.demorgan.projectpractice.store.dto.CompanyDto;
import xyz.demorgan.projectpractice.store.dto.input.CompanyInputDto;
import xyz.demorgan.projectpractice.store.mapper.CompanyMapper;

import java.util.List;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor
@Controller
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Validated
public class CompanyController {
    CompanyService companyService;
    CompanyMapper companyMapper;
    Validator validator;

    @QueryMapping
    public List<CompanyDto> companies() {
        return companyService.getAll();
    }

    @QueryMapping
    public CompanyDto company(@Argument Long id) {
        return companyService.getById(id);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @QueryMapping
    public List<CompanyDto> unapprovedCompanies() {
        return companyService.findUnapprovedCompanies();
    }

    @MutationMapping
    public CompanyDto createCompany(@Argument("input") @Valid CompanyInputDto companyInputDto) {
        Set<ConstraintViolation<CompanyInputDto>> violations = validator.validate(companyInputDto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        return companyMapper.toCompanyDto(companyService.create(companyInputDto));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @MutationMapping
    public CompanyDto deleteCompany(@Argument("id") Long id) {
        return companyService.delete(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @MutationMapping
    public void deleteAllCompanies() {
        companyService.deleteAllCompanies();
    }
}
