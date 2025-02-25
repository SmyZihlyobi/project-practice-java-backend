package xyz.demorgan.projectpractice.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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

    public List<StudentDto> getAll() {
        log.info("Getting all students at {}", System.currentTimeMillis());
        return studentRepository.findAll()
                .stream()
                .map(studentMapper::toStudentDto)
                .collect(Collectors.toList());
    }

    public void getById(Long id) {
        log.info("Getting student with id: {} at {}", id, System.currentTimeMillis());
        studentMapper.toStudentDto(studentRepository.findById(id)
                .orElseThrow(() -> new NotFound("Student with id " + id + " not found")));
    }

    @Transactional
    public StudentDto addStudent(StudentInputDto studentInputDto) {
        log.info("Adding student at {}", System.currentTimeMillis());
        Student student = studentMapper.toEntity(studentInputDto);

        Team team = teamRepository.findByNameIgnoreCase(studentInputDto.getTeamName());
        if (team == null) {
            team = new Team();
            team.setName(studentInputDto.getTeamName());
            team = teamRepository.save(team);
        }
        student.setTeam(team);
        student.setCreatedAt(LocalDateTime.now());

        student = studentRepository.save(student);
        return studentMapper.toStudentDto(student);
    }

    @Transactional
    public StudentDto deleteStudent(Long id) {
        log.info("Deleting student with id: {} at {}", id, System.currentTimeMillis());
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new NotFound("Student with id " + id + " not found"));
        studentRepository.delete(student);
        return studentMapper.toStudentDto(student);
    }
}
