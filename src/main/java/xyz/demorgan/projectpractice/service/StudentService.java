package xyz.demorgan.projectpractice.service;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xyz.demorgan.projectpractice.exceptions.NotFound;
import xyz.demorgan.projectpractice.store.dto.StudentDto;
import xyz.demorgan.projectpractice.store.mapper.StudentMapper;
import xyz.demorgan.projectpractice.store.repos.StudentRepository;

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
}
