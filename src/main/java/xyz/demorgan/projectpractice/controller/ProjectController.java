package xyz.demorgan.projectpractice.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import xyz.demorgan.projectpractice.service.ProjectService;
import xyz.demorgan.projectpractice.store.dto.ProjectDto;
import xyz.demorgan.projectpractice.store.dto.input.ProjectInputDto;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor
@Controller
@Validated
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ProjectController {
    HttpServletRequest request;
    ProjectService projectService;
    Validator validator;

    @QueryMapping
    public List<ProjectDto> projects() {
        return projectService.getAll();
    }

    @QueryMapping
    public ProjectDto project(@Argument("id") Long id) {
        return projectService.getById(id);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_COMPANY')")
    @MutationMapping
    public ProjectDto createProject(@Argument @Valid ProjectInputDto input) {
        Set<ConstraintViolation<ProjectInputDto>> violations = validator.validate(input);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        String token = request.getHeader("Authorization");
        if (token == null) {
            throw new RuntimeException("Authorization header is null");
        }
        return projectService.create(input, token);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @MutationMapping
    public void deleteProject(@Argument("id") Long id) {
        projectService.delete(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @MutationMapping
    public void deleteAllProjects() {
        projectService.deleteAllProjects();
    }
}
