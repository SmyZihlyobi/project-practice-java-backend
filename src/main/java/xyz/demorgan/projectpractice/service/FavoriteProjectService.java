package xyz.demorgan.projectpractice.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xyz.demorgan.projectpractice.exceptions.NotFound;
import xyz.demorgan.projectpractice.store.dto.FavoriteProjectDto;
import xyz.demorgan.projectpractice.store.dto.input.FavoriteProjectInput;
import xyz.demorgan.projectpractice.store.entity.FavoriteProject;
import xyz.demorgan.projectpractice.store.mapper.FavoriteProjectMapper;
import xyz.demorgan.projectpractice.store.repos.FavoriteProjectRepository;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@RequiredArgsConstructor
@Service
@FieldDefaults(level = PRIVATE)
public class FavoriteProjectService {
    final FavoriteProjectMapper favoriteProjectMapper;
    final FavoriteProjectRepository favoriteProjectRepository;

    public List<FavoriteProjectDto> getFavoriteProjects(Long id) {
        log.info("Fetching favorite projects for student with id: {}", id);
        return favoriteProjectRepository
                .findAllByStudentId((id))
                .orElseThrow(() -> new NotFound("No favorite projects found for student with id: " + id))
                .stream()
                .map(favoriteProjectMapper::toFavoriteProjectDto)
                .toList();
    }

    public FavoriteProjectDto addFavoriteProject(FavoriteProjectInput input) {
        log.info("Adding favorite project: {}", input);
        FavoriteProject favoriteProject = favoriteProjectMapper.toEntity(input);
        return favoriteProjectMapper.toFavoriteProjectDto(favoriteProjectRepository.save(favoriteProject));
    }

    public FavoriteProjectDto removeFavoriteProject(Long studentId, Long projectId) {
        log.info("Removing favorite project with studentId: {} and projectId: {}", studentId, projectId);
        FavoriteProject favoriteProject = favoriteProjectRepository
                .findAllByStudentId(studentId)
                .orElseThrow(() -> new NotFound("No favorite projects found for student with id: " + studentId))
                .stream()
                .filter(fp -> fp.getProjectId().equals(projectId))
                .findFirst()
                .orElseThrow(() -> new NotFound("No favorite project found with id: " + projectId));
        favoriteProjectRepository.delete(favoriteProject);
        return favoriteProjectMapper.toFavoriteProjectDto(favoriteProject);
    }
}
