package xyz.demorgan.projectpractice.exceptions;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

@Slf4j
public class NotFound extends RuntimeException {
    public NotFound(String message) {
        super(message);
        HashMap<String, Object> logData = new HashMap<>();

        logData.put("message", message);
        logData.put("exception", this.getClass().getName());
        logData.put("stacktrace", this.getStackTrace());
        logData.put("cause", this.getCause());
        logData.put("timestamp", System.currentTimeMillis());

        log.error(logData.toString());
    }
}