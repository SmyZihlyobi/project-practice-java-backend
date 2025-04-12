package xyz.demorgan.projectpractice.store.dto.input;

import lombok.Data;

@Data
public class StudentRegistrationInput {
    private String username;
    private String password;
    Boolean rememberMe = false;
}
