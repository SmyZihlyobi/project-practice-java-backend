package xyz.demorgan.projectpractice.controller;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import xyz.demorgan.projectpractice.service.TeamService;
import xyz.demorgan.projectpractice.store.dto.TeamDto;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor
@Controller
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class TeamController {
    TeamService teamService;

    @QueryMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STUDENT', 'ROLE_COMPANY')")
    public List<TeamDto> teams() {
        return teamService.getAll();
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STUDENT', 'ROLE_COMPANY')")
    @QueryMapping
    public TeamDto team(@Argument Long id) {
        return teamService.getById(id);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @MutationMapping
    public TeamDto deleteTeam(@Argument Long id) {
        return teamService.deleteTeam(id);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @MutationMapping
    public void deleteAllTeams() {
        teamService.deleteAllTeams();
    }
}
