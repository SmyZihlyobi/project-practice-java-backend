package xyz.demorgan.projectpractice.controller;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import xyz.demorgan.projectpractice.service.TeamService;
import xyz.demorgan.projectpractice.store.dto.TeamDto;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor
@Controller
@Validated
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class TeamController {
    TeamService teamService;

    @QueryMapping
    public List<TeamDto> teams() {
        return teamService.getAll();
    }

    @QueryMapping
    public TeamDto team(@Argument Long id) {
        return teamService.getById(id);
    }

    @MutationMapping
    public void deleteTeam(@Argument Long id) {
        teamService.deleteTeam(id);
    }
}
