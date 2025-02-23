package xyz.demorgan.projectpractice.controller;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import xyz.demorgan.projectpractice.service.ProjectService;
import xyz.demorgan.projectpractice.store.dto.ProjectDto;
import xyz.demorgan.projectpractice.store.dto.input.ProjectInputDto;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor
@Controller
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ProjectController {
    ProjectService projectService;

    @QueryMapping
    public List<ProjectDto> projects() {
        return projectService.getAll();
    }

    @QueryMapping
    public ProjectDto project(@Argument("id") Long id) {
        return projectService.getById(id);
    }

    @MutationMapping
    public ProjectDto createProject(@Argument ProjectInputDto input, @RequestHeader("Authorization") String token) {
        return projectService.create(input, token);
    }

    @MutationMapping
    public void deleteProject(@Argument("id") Long id) {
        projectService.delete(id);
    }
}
