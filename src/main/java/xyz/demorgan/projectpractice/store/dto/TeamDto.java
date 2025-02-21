package xyz.demorgan.projectpractice.store.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for {@link xyz.demorgan.projectpractice.store.entity.Team}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamDto {
    private Long id;
    private String name;
    private List<StudentDto> students;
}