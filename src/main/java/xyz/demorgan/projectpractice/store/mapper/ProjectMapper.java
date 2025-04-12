package xyz.demorgan.projectpractice.store.mapper;

import org.mapstruct.*;
import xyz.demorgan.projectpractice.store.dto.ProjectDto;
import xyz.demorgan.projectpractice.store.dto.input.ProjectInputDto;
import xyz.demorgan.projectpractice.store.entity.Project;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProjectMapper {
    Project toEntity(ProjectDto projectDto);

    ProjectDto toProjectDto(Project project);

    Project toEntity(ProjectInputDto projectInputDto);

    @AfterMapping
    default void setDefaultValues(@MappingTarget Project project) {
        if (project.getActive() == null) {
            project.setActive(true);
        }
    }
}