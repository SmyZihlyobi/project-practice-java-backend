package xyz.demorgan.projectpractice.controller;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import xyz.demorgan.projectpractice.service.StudentService;
import xyz.demorgan.projectpractice.store.dto.StudentDto;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor
@Controller
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class StudentController {
    StudentService studentService;

    @QueryMapping
    public List<StudentDto> students() {
        return studentService.getAll();
    }

    @QueryMapping
    public void student(@Argument Long id) {
        studentService.getById(id);
    }
}
