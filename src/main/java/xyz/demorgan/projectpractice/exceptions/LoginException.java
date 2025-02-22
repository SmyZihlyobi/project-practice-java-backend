package xyz.demorgan.projectpractice.exceptions;

import lombok.Data;

import java.util.Date;

@Data
public class LoginException {
    private int status;
    private String message;
    private Date timestamp;

    public LoginException(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = new Date();
    }
}
