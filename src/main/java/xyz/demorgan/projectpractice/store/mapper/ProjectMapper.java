package xyz.demorgan.projectpractice.store.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import xyz.demorgan.projectpractice.store.entity.Project;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProjectMapper {
    Project toEntity(xyz.demorgan.projectpractice.store.dto.ProjectDto projectDto);

    xyz.demorgan.projectpractice.store.dto.ProjectDto toProjectDto(Project project);
}