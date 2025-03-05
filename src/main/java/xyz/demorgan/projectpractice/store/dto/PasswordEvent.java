package xyz.demorgan.projectpractice.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class PasswordEvent implements Serializable {
    private String email;
    private String password;
    private boolean isFirstApprove = false;
}
