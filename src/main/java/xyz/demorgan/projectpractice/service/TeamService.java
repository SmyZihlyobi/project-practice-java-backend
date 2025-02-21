package xyz.demorgan.projectpractice.service;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xyz.demorgan.projectpractice.exceptions.NotFound;
import xyz.demorgan.projectpractice.store.dto.TeamDto;
import xyz.demorgan.projectpractice.store.mapper.TeamMapper;
import xyz.demorgan.projectpractice.store.repos.TeamRepository;

import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@AllArgsConstructor
@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class TeamService {
    private final TeamMapper teamMapper;
    TeamRepository teamRepository;

    public List<TeamDto> getAll() {
        log.info("Getting all teams at {}", System.currentTimeMillis());
        return teamRepository.findAll()
                .stream()
                .map(teamMapper::toTeamDto)
                .collect(Collectors.toList());
    }

    public TeamDto getById(Long id) {
        log.info("Getting team with id: {} at {}", id, System.currentTimeMillis());
        return teamMapper.toTeamDto(teamRepository.findById(id)
                .orElseThrow(() -> new NotFound("Team with id " + id + " not found")));
    }
}
