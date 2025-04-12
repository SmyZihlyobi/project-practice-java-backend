package xyz.demorgan.projectpractice.store.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for {@link xyz.demorgan.projectpractice.store.entity.FavoriteProject}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteProjectDto {
    private Long projectId;
}