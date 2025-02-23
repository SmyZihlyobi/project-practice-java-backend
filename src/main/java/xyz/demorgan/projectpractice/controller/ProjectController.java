package xyz.demorgan.projectpractice.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.stereotype.Controller;
import xyz.demorgan.projectpractice.service.ProjectService;
import xyz.demorgan.projectpractice.store.dto.ProjectDto;
import xyz.demorgan.projectpractice.store.dto.input.ProjectInputDto;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor
@Controller
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ProjectController {
    HttpServletRequest request;
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
    public ProjectDto createProject(@Argument ProjectInputDto input) {
        String token = request.getHeader("Authorization");
        if (token == null) {
            throw new RuntimeException("Authorization header is null");
        }
        return projectService.create(input, token);
    }

    @MutationMapping
    public void deleteProject(@Argument("id") Long id) {
        projectService.delete(id);
    }
}
