package xyz.demorgan.projectpractice.store.mapper;

import org.mapstruct.*;
import xyz.demorgan.projectpractice.store.dto.input.CompanyInputDto;
import xyz.demorgan.projectpractice.store.dto.CompanyDto;
import xyz.demorgan.projectpractice.store.entity.Company;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {ProjectMapper.class})
public interface CompanyMapper {
    Company toEntity(CompanyDto companyDto);

    @AfterMapping
    default void linkProjects(@MappingTarget Company company) {
        company.getProjects().forEach(project -> project.setCompany(company));
    }

    CompanyDto toCompanyDto(Company company);

    Company toEntity(CompanyInputDto companyInputDto);
}