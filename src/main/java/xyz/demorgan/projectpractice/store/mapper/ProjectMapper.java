package xyz.demorgan.projectpractice.store.mapper;

import org.mapstruct.*;
import xyz.demorgan.projectpractice.store.dto.ProjectDto;
import xyz.demorgan.projectpractice.store.dto.input.ProjectInputDto;
import xyz.demorgan.projectpractice.store.entity.Project;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProjectMapper {
    Project toEntity(ProjectDto projectDto);

    @Mapping(target = "companyName", source = "company.name")
    ProjectDto toProjectDto(Project project);

    Project toEntity(ProjectInputDto projectInputDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateProjectFromDto(ProjectInputDto projectInputDto, @MappingTarget Project project);
}