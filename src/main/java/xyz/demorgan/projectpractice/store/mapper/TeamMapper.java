package xyz.demorgan.projectpractice.store.mapper;

import org.mapstruct.*;
import xyz.demorgan.projectpractice.store.dto.TeamDto;
import xyz.demorgan.projectpractice.store.entity.Team;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {StudentMapper.class})
public interface TeamMapper {
    Team toEntity(TeamDto teamDto);

    @AfterMapping
    default void linkStudents(@MappingTarget Team team) {
        team.getStudents().forEach(student -> student.setTeam(team));
    }

    TeamDto toTeamDto(Team team);
}