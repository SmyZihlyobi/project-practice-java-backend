package xyz.demorgan.projectpractice.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import xyz.demorgan.projectpractice.exceptions.NotFound;
import xyz.demorgan.projectpractice.store.dto.TeamDto;
import xyz.demorgan.projectpractice.store.entity.Student;
import xyz.demorgan.projectpractice.store.entity.Team;
import xyz.demorgan.projectpractice.store.mapper.TeamMapper;
import xyz.demorgan.projectpractice.store.repos.StudentRepository;
import xyz.demorgan.projectpractice.store.repos.TeamRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@AllArgsConstructor
@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class TeamService {
    private final TeamMapper teamMapper;
    TeamRepository teamRepository;
    StudentRepository studentRepository;

    public List<TeamDto> getAll() {
        log.info("Getting all teams at {}", System.currentTimeMillis());
        return teamRepository.findAllByOrderByNameAsc()
                .stream()
                .map(teamMapper::toTeamDto)
                .collect(Collectors.toList());
    }

    public TeamDto getById(Long id) {
        log.info("Getting team with id: {} at {}", id, System.currentTimeMillis());
        return teamMapper.toTeamDto(teamRepository.findById(id)
                .orElseThrow(() -> new NotFound("Team with id " + id + " not found")));
    }

    @Transactional
    public TeamDto deleteTeam(Long id) {
        log.info("Deleting team with id: {} at {}", id, System.currentTimeMillis());
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new NotFound("Team with id " + id + " not found"));

        String defaultTeamName = "Не выбрана";

        if (team.getName().equalsIgnoreCase(defaultTeamName)) {
            throw new IllegalArgumentException("Нельзя удалить команду по умолчанию");
        }

        Team defaultTeam = teamRepository.findByNameIgnoreCase(defaultTeamName);
        if (defaultTeam == null) {
            defaultTeam = new Team();
            defaultTeam.setName(defaultTeamName);
            teamRepository.save(defaultTeam);
            teamRepository.flush();
        }

        List<Student> students = studentRepository.findAllByTeam(team);
        Team finalDefaultTeam = defaultTeam;
        students.forEach(student -> student.setTeam(finalDefaultTeam));
        studentRepository.saveAll(students);

        teamRepository.delete(team);

        return teamMapper.toTeamDto(team);
    }

    @Transactional
    public Map<String, String> deleteAllTeams() {
        log.info("Deleting all teams at {}", System.currentTimeMillis());
        Map<String, String> response = new HashMap<>();
        String defaultTeamName = "Не выбрана";

        try {
            Team defaultTeam = teamRepository.findByNameIgnoreCase(defaultTeamName);
            if (defaultTeam == null) {
                defaultTeam = new Team();
                defaultTeam.setName(defaultTeamName);
                teamRepository.save(defaultTeam);
                teamRepository.flush();
            }

            List<Team> teams = teamRepository.findAll();
            teams.removeIf(team -> team.getName().equalsIgnoreCase(defaultTeamName));

            for (Team team : teams) {
                List<Student> students = studentRepository.findAllByTeam(team);
                for (Student student : students) {
                    student.setTeam(defaultTeam);
                    studentRepository.save(student);
                }
                teamRepository.delete(team);
            }

            response.put("message", "Все команды успешно удалены, кроме команды по умолчанию.");
        } catch (Exception e) {
            log.error("Error deleting all teams: {}", e.getMessage());
            response.put("message", "Ошибка при удалении команд: " + e.getMessage());
        }

        return response;
    }
}
