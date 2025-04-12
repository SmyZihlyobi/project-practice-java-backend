package xyz.demorgan.projectpractice.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import xyz.demorgan.projectpractice.service.StudentService;
import xyz.demorgan.projectpractice.store.dto.LoginAnswer;
import xyz.demorgan.projectpractice.store.dto.input.StudentRegistrationInput;

@AllArgsConstructor
@FieldDefaults(makeFinal = true)
@RestController
@Tag(name = "Student auth controller", description = "Controller for student authentication")
@Slf4j
@Validated
public class StudentAuthController {
    StudentService studentService;

    @PostMapping("/student/register")
    public ResponseEntity<LoginAnswer> register(@Valid @RequestBody StudentRegistrationInput student) {
        log.info("Registering student: {}", student);
        String jwtToken = studentService.registerOrLoginStudent(
                student.getUsername(),
                student.getPassword(),
                student.getRememberMe()
        );

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new LoginAnswer(jwtToken));
    }
}
