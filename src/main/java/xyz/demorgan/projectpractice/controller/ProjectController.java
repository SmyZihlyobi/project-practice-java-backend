package xyz.demorgan.projectpractice.controller;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import xyz.demorgan.projectpractice.service.ProjectService;
import xyz.demorgan.projectpractice.store.dto.ProjectDto;

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
}
