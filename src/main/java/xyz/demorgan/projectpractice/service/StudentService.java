package xyz.demorgan.projectpractice.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xyz.demorgan.projectpractice.config.jwt.JwtTokenUtils;
import xyz.demorgan.projectpractice.exceptions.NotFound;
import xyz.demorgan.projectpractice.store.dto.StudentDto;
import xyz.demorgan.projectpractice.store.dto.input.StudentInputDto;
import xyz.demorgan.projectpractice.store.entity.Student;
import xyz.demorgan.projectpractice.store.entity.Team;
import xyz.demorgan.projectpractice.store.mapper.StudentMapper;
import xyz.demorgan.projectpractice.store.repos.StudentRepository;
import xyz.demorgan.projectpractice.store.repos.TeamRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@AllArgsConstructor
@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class StudentService {
    private final StudentMapper studentMapper;
    StudentRepository studentRepository;
    TeamRepository teamRepository;
    JwtTokenUtils jwtTokenUtils;

    public List<StudentDto> getAll() {
        log.info("Getting all students at {}", System.currentTimeMillis());
        return studentRepository.findAll().stream().map(studentMapper::toStudentDto).collect(Collectors.toList());
    }

    public void getById(Long id) {
        log.info("Getting student with id: {} at {}", id, System.currentTimeMillis());
        studentMapper.toStudentDto(studentRepository.findById(id).orElseThrow(() -> new NotFound("Student with id " + id + " not found")));
    }

    @Transactional
    public StudentDto addStudent(StudentInputDto studentInputDto) {
        log.info("Adding student at {}", System.currentTimeMillis());

        Student studentUsername = studentRepository.findByUsername(studentInputDto.getUsername());

        Student student = studentMapper.toEntity(studentInputDto);

        Team team;
        if (studentInputDto.getTeamName() != null) {
            team = teamRepository.findByNameIgnoreCase(studentInputDto.getTeamName());
            if (team == null) {
                team = new Team();
                team.setName(studentInputDto.getTeamName());
                team = teamRepository.save(team);
            }
        } else {
            String defaultTeamName = "Не выбрана";
            team = teamRepository.findByNameIgnoreCase(defaultTeamName);
            if (team == null) {
                team = new Team();
                team.setName(defaultTeamName);
                team = teamRepository.save(team);
            }
        }

        student.setId(studentUsername.getId());
        student.setUsername(studentUsername.getUsername());
        student.setTeam(team);

        if (student.getCreatedAt() == null) {
            student.setCreatedAt(LocalDateTime.now());
        }

        student = studentRepository.save(student);
        return studentMapper.toStudentDto(student);
    }

    @Transactional
    public String registerOrLoginStudent(String username, String password, boolean rememberMe) {
        log.info("Registering or logging in student with username: {} at {}", username, System.currentTimeMillis());

        if (!OnlinePsuAuthService.validateUser(username, password)) {
            throw new IllegalArgumentException("Test.psu auth failed for username: " + username);
        }

        Student student = studentRepository.findByUsername(username);
        if (student == null) {
            student = new Student();
            student.setUsername(username);
            student.setCreatedAt(LocalDateTime.now());
            student = studentRepository.save(student);
        }

        if (student.getCreatedAt() == null) {
            student.setCreatedAt(LocalDateTime.now());
        }

        return jwtTokenUtils.generateStudentToken(student, rememberMe);
    }


    @Transactional
    public StudentDto deleteStudent(Long id) {
        log.info("Deleting student with id: {} at {}", id, System.currentTimeMillis());
        Student student = studentRepository.findById(id).orElseThrow(() -> new NotFound("Student with id " + id + " not found"));
        studentRepository.delete(student);
        return studentMapper.toStudentDto(student);
    }

    @Transactional
    public void deleteAllStudents() {
        log.info("Deleting all students at {}", System.currentTimeMillis());
        try {
            studentRepository.deleteAll();
        } catch (Exception e) {
            log.error("Error deleting all students: {}", e.getMessage());
        }
    }

    @Transactional
    public StudentDto changeTeam(String jwtToken, Long teamId) {
        log.info("Changing student team at {}", System.currentTimeMillis());
        String token = jwtToken.replace("Bearer ", "");
        Long studentId = Long.valueOf(jwtTokenUtils.getIdFromToken(token));

        Student student = studentRepository.findById(studentId).orElseThrow(() -> new NotFound("Student with id " + studentId + " not found"));

        Team team = teamRepository.findById(teamId).orElseThrow(() -> new NotFound("Team with id " + teamId + " not found"));

        student.setTeam(team);

        return studentMapper.toStudentDto(studentRepository.save(student));
    }
}
