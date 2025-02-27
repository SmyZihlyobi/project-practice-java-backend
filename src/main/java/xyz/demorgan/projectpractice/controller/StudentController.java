package xyz.demorgan.projectpractice.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import xyz.demorgan.projectpractice.service.StudentService;
import xyz.demorgan.projectpractice.store.dto.StudentDto;
import xyz.demorgan.projectpractice.store.dto.input.StudentInputDto;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor
@Controller
@Validated
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class StudentController {
    StudentService studentService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_COMPANY', 'ROLE_STUDENT')")
    @QueryMapping
    public List<StudentDto> students() {
        return studentService.getAll();
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_COMPANY', 'ROLE_STUDENT')")
    @QueryMapping
    public void student(@Argument Long id) {
        studentService.getById(id);
    }

    @MutationMapping
    public StudentDto createStudent(@Argument("input") @Valid StudentInputDto studentInputDto) {
        return studentService.addStudent(studentInputDto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @MutationMapping
    public StudentDto deleteStudent(@Argument Long id) {
        return studentService.deleteStudent(id);
    }
}
