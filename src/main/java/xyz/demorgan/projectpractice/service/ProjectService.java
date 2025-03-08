package xyz.demorgan.projectpractice.service;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import xyz.demorgan.projectpractice.config.jwt.JwtTokenUtils;
import xyz.demorgan.projectpractice.exceptions.NotFound;
import xyz.demorgan.projectpractice.store.dto.ProjectDto;
import xyz.demorgan.projectpractice.store.dto.input.ProjectInputDto;
import xyz.demorgan.projectpractice.store.entity.Company;
import xyz.demorgan.projectpractice.store.entity.Project;
import xyz.demorgan.projectpractice.store.mapper.ProjectMapper;
import xyz.demorgan.projectpractice.store.repos.CompanyRepository;
import xyz.demorgan.projectpractice.store.repos.ProjectRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@AllArgsConstructor
@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ProjectService {
    ProjectMapper projectMapper;
    JwtTokenUtils jwtTokenUtils;
    ProjectRepository projectRepository;
    CompanyRepository companyRepository;

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

    @CacheEvict(value = "projects", allEntries = true)
    public ProjectDto create(ProjectInputDto input, String jwtToken) {
        // TODO отправка ид студ проекта на почту через кафку
        log.info("Creating project at {}", System.currentTimeMillis());

        String token = jwtToken.replace("Bearer ", "");
        Long companyId = Long.valueOf(jwtTokenUtils.getIdFromToken(token));

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new NotFound("Company with id " + companyId + " not found"));

        Project project = projectMapper.toEntity(input);
        project.setCompany(company);
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());

        return projectMapper.toProjectDto(projectRepository.save(project));
    }

    @CacheEvict(value = "projects", allEntries = true)
    public void delete(Long id) {
        log.info("Deleting project with id: {} at {}", id, System.currentTimeMillis());
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new NotFound("Project with id " + id + " not found"));
        projectRepository.delete(project);
    }


    @CacheEvict(value = "projects", allEntries = true)
    public void deleteAllProjects() {
        log.info("Deleting all projects at {}", System.currentTimeMillis());
        projectRepository.deleteAll();
    }
}
