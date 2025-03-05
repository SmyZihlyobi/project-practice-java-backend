package xyz.demorgan.projectpractice.store.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class PasswordEvent {
    private String email;
    private String password;
}
