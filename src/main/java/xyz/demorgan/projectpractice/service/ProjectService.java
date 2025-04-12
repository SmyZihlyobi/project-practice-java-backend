package xyz.demorgan.projectpractice.service;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import xyz.demorgan.projectpractice.config.KafkaConfig;
import xyz.demorgan.projectpractice.config.jwt.JwtTokenUtils;
import xyz.demorgan.projectpractice.exceptions.NotFound;
import xyz.demorgan.projectpractice.store.dto.ProjectDto;
import xyz.demorgan.projectpractice.store.dto.StudentProjectCreationEvent;
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
    KafkaTemplate<String, StudentProjectCreationEvent> kafkaTemplate;


    @Cacheable(value = "projects", key = "'all_projects'")
    public List<ProjectDto> getAll() {
        log.info("Getting all projects at {}", System.currentTimeMillis());
        return projectRepository.findAllSorted()
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
    public ProjectDto update(Long id, ProjectInputDto input, String jwtToken) {
        log.info("Updating project with id: {} at {}", id, System.currentTimeMillis());

        String token = jwtToken.replace("Bearer ", "");
        Long companyId = Long.valueOf(jwtTokenUtils.getIdFromToken(token));

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new NotFound("Project with id " + id + " not found"));

        if (!project.getCompany().getId().equals(companyId)) {
            throw new RuntimeException("You are not authorized to update this project");
        }

        projectMapper.updateProjectFromDto(input, project);
        project.setUpdatedAt(LocalDateTime.now());

        Project updatedProject = projectRepository.save(project);
        return projectMapper.toProjectDto(updatedProject);
    }

    @CacheEvict(value = "projects", allEntries = true)
    public ProjectDto create(ProjectInputDto input, String jwtToken) {
        log.info("Creating project at {}", System.currentTimeMillis());

        String token = jwtToken.replace("Bearer ", "");
        Long companyId = Long.valueOf(jwtTokenUtils.getIdFromToken(token));

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new NotFound("Company with id " + companyId + " not found"));

        Project project = projectMapper.toEntity(input);
        project.setCompany(company);
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());

        Project savedProject = projectRepository.save(project);

        if (input.isStudentProject()) {
            StudentProjectCreationEvent event = new StudentProjectCreationEvent(
                    company.getEmail(),
                    savedProject.getId()
            );
            kafkaTemplate.send(KafkaConfig.PROJECT_ID_TOPIC, event);
        }

        return projectMapper.toProjectDto(savedProject);
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

    @CacheEvict(value = "projects", allEntries = true)
    public void archiveProject(Long id) {
        log.info("Archiving project with id: {} at {}", id, System.currentTimeMillis());
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new NotFound("Project with id " + id + " not found"));
        project.setActive(false);
        projectRepository.save(project);
    }

    @CacheEvict(value = "projects", allEntries = true)
    public void unarchiveProject(Long id) {
        log.info("Unarchiving project with id: {} at {}", id, System.currentTimeMillis());
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new NotFound("Project with id " + id + " not found"));
        project.setActive(true);
        projectRepository.save(project);
    }

    @CacheEvict(value = "projects", allEntries = true)
    public void archiveAllProjects() {
        log.info("Archiving all projects at {}", System.currentTimeMillis());
        List<Project> projects = projectRepository.findAll();
        for (Project project : projects) {
            project.setActive(false);
        }
        projectRepository.saveAll(projects);
    }
}
