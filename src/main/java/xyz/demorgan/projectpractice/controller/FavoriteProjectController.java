package xyz.demorgan.projectpractice.controller;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import xyz.demorgan.projectpractice.service.FavoriteProjectService;
import xyz.demorgan.projectpractice.store.dto.FavoriteProjectDto;
import xyz.demorgan.projectpractice.store.dto.input.FavoriteProjectInput;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;


@AllArgsConstructor
@Controller
@Validated
@FieldDefaults(level = PRIVATE)
public class FavoriteProjectController {
    FavoriteProjectService favoriteProjectService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STUDENT')")
    @QueryMapping
    public List<FavoriteProjectDto> favoriteProjects(@Argument Long id) {
        return favoriteProjectService.getFavoriteProjects(id);
    }

    @PreAuthorize("hasAnyRole('ROLE_STUDENT')")
    @MutationMapping
    public FavoriteProjectDto addFavoriteProject(@Argument FavoriteProjectInput input) {
        return favoriteProjectService.addFavoriteProject(input);
    }

    @PreAuthorize("hasAnyRole('ROLE_STUDENT')")
    @MutationMapping
    public FavoriteProjectDto removeFavoriteProject(@Argument Long studentId, @Argument Long projectId) {
        return favoriteProjectService.removeFavoriteProject(studentId, projectId);
    }
}
