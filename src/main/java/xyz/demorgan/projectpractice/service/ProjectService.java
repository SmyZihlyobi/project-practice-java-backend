package xyz.demorgan.projectpractice.service;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import xyz.demorgan.projectpractice.exceptions.NotFound;
import xyz.demorgan.projectpractice.store.dto.ProjectDto;
import xyz.demorgan.projectpractice.store.mapper.ProjectMapper;
import xyz.demorgan.projectpractice.store.repos.ProjectRepository;

import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@AllArgsConstructor
@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ProjectService {
    private final ProjectMapper projectMapper;
    ProjectRepository projectRepository;

    @Cacheable(value = "projects", key = "'all_projects'")
    public List<ProjectDto> getAll() {
        log.info("Getting all projects at {}", System.currentTimeMillis());
        return projectRepository.findAll()
                .stream()
                .map(projectMapper::toProjectDto)
                .collect(Collectors.toList());
    }

    public ProjectDto getById(Long id) {
        log.info("Getting project with id: {} at {}", id, System.currentTimeMillis());
        return projectMapper.toProjectDto(projectRepository.findById(id)
                .orElseThrow(() -> new NotFound("Project with id " + id + " not found")));
    }
}
