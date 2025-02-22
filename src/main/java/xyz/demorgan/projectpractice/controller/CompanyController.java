package xyz.demorgan.projectpractice.controller;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import xyz.demorgan.projectpractice.service.CompanyService;
import xyz.demorgan.projectpractice.store.dto.CompanyDto;
import xyz.demorgan.projectpractice.store.dto.input.CompanyInputDto;
import xyz.demorgan.projectpractice.store.mapper.CompanyMapper;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor
@Controller
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CompanyController {
    CompanyService companyService;
    CompanyMapper companyMapper;

    @Cacheable(value = "companies")
    @QueryMapping
    public List<CompanyDto> companies() {
        return companyService.getAll();
    }

    @QueryMapping
    public CompanyDto company(@Argument Long id) {
        return companyService.getById(id);
    }

    @CacheEvict(value = "companies", allEntries = true)
    @MutationMapping
    public CompanyDto createCompany(@Argument("input") CompanyInputDto companyInputDto) {
        return companyMapper.toCompanyDto(companyService.create(companyInputDto));
    }

    @CacheEvict(value = "companies", allEntries = true)
    @MutationMapping
    public CompanyDto deleteCompany(@Argument("id") Long id) {
        return companyService.delete(id);
    }
}
