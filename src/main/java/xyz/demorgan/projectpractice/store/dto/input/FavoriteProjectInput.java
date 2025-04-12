package xyz.demorgan.projectpractice.store.dto.input;

import lombok.Data;

@Data
public class FavoriteProjectInput {
    private Long studentId;
    private Long projectId;
}
